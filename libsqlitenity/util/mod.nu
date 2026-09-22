const CONFIG_PATH = path self | path dirname | path dirname
const CONFIG_FILE = $CONFIG_PATH | path join config.toml

export def download-sqlite3mc [
  file: path,
  --force
]: nothing -> nothing {
  let config = open $CONFIG_FILE

  let url = $"($config.sqlite3mc.repository)/archive/refs/tags/v($config.sqlite3mc.version).tar.gz"
  let sha256 = $config.sqlite3mc.sha256

  download $url $sha256 $file --force=($force)
}

export def download-sqlite3mc-wasm [
  file: path,
  --force
]: nothing -> nothing {
  let config = open $CONFIG_FILE

  let url = $"($config.sqlite3mc.repository)/releases/download/v($config.sqlite3mc.version)/sqlite3mc-($config.sqlite3mc.version)-sqlite-($config.sqlite3mc.sqlite3)-wasm.zip"
  let sha256 = $config.sqlite3mc.wasm.sha256

  download $url $sha256 $file --force=($force)
}

export def extract-sqlite3mc [
  file: path,
  directory: path
]: nothing -> nothing {
  let config = open $CONFIG_FILE

  let name = $"SQLite3MultipleCiphers-($config.sqlite3mc.version)"

  let src = [ $name src ] | path join
  let amalgamate = [ $name scripts amalgamate.py ] | path join
  let sqlite3mc_c = [ $name scripts sqlite3mc.c.json ] | path join
  let sqlite3mc_h = [ $name scripts sqlite3mc.h.json ] | path join

  let paths = [ $src $amalgamate $sqlite3mc_c $sqlite3mc_h ]

  rm -rf $directory

  extract $file ...($paths) --directory=($directory) --strip-components=1
}

export def extract-sqlite3mc-wasm [
  file: path,
  directory: path
]: nothing -> nothing {
  let config = open $CONFIG_FILE

  let name = $"sqlite3mc-wasm-(semver-to-int $config.sqlite3mc.sqlite3)"

  let sqlite3_mjs = [ $name jswasm sqlite3.mjs ] | path join
  let sqlite3_wasm = [ $name jswasm sqlite3.wasm ] | path join

  let paths = [ $sqlite3_mjs $sqlite3_wasm ]

  rm -rf $directory

  extract $file ...($paths) --directory=($directory) --strip-components=2
}

export def amalgamate [
  script: path,
  config: path,
  source: path,
  target: path,
]: nothing -> nothing {
  mkdir ($target | path dirname)

  let tmpconfig = mktemp -t

  open $config
  | upsert target { $target }
  | to json
  | save -f $tmpconfig

  let output =  ^python3 $script --config=($tmpconfig) --source=($source) 
  | complete

  if $output.exit_code != 0 {
    error make --unspanned { msg: $output.stderr }
  }

  rm $tmpconfig
} 

export def download-jni-headers [
  directory: path,
]: nothing -> nothing {
  let config = open $CONFIG_FILE

  let version = $config.jdk.version
  let files = $config.jdk.files
  let repository = $config.jdk.repository

  for $it in $files {
    let url = $"($repository)/($version)/($it.source)"
    let sha256 = $it.sha256
    let target = $"($directory)/($it.target)"

    download $url $sha256 $target
  }
}

export def deps [
  out: path,
  ...sources: path,
]: nothing -> nothing {
  mkdir ($out | path dirname)

  let depfile = $"($out).d"

  touch $out

  let all = [ util/mod.nu ] | append $sources | str join " "
  $"($out): ($all)" | save -f $depfile
}

def normalize-directory [
  directory: path
]: nothing -> nothing {
  let glob = $"($directory)/**"

  glob $glob | relativize-symlinks
  glob $glob | remove-empty-dirs
}

export def download-ndk [
  directory?: path,
  --deps: path,
  --force,
]: nothing -> path {
  let config = open $CONFIG_FILE

  let kind = match $nu.os-info.name {
    linux => "linux",
    macos => "darwin",
    _     => { error make --unspanned { msg: $"Unsupported OS: ($nu.os-info.name)" } },
  }

  let deps = $deps
  | default { cd $CONFIG_PATH; "deps" | path expand }
  | path expand

  let name = $"android-ndk-($config.ndk.version)-($kind)"
  let filename = $"($name).zip"
  let file = [ $deps $name ] | path join
  let url = $"https://dl.google.com/android/repository/($filename)"
  let sha256 = $config.ndk.sha256 | get $kind

  let file = [ $deps $filename ] | path join
  let directory = $directory 
  | default { [ $deps $name ] | path join }
  | path expand

  if (not $force) and ($directory | path exists) {
    error make --unspanned { 
        msg: $"($directory) already exists", 
    }
  }
  
  rm -rf $directory
  mkdir $directory

  download $url $sha256 $file
  extract $file --directory=($directory) --strip-components=1

  $directory
}

export def download-sysroot [
  directory?: path,
  --deps: path,
  --force,
]: nothing -> path {
  let config = open $CONFIG_FILE

  let directory = $directory 
  | default { cd $CONFIG_PATH; $config.directory | path expand }
  | path expand

  let deps = $deps
  | default { cd $CONFIG_PATH; $config.deps | path expand }
  | path expand

  if (not $force) and ($directory | path exists) {
    error make --unspanned { 
        msg: $"($directory) already exists", 
    }
  }

  rm -rf $directory
  mkdir $directory

  let sysroots = $config.sysroots
  | upsert name  {|row| $"($row.source).tar.gz"             }
  | upsert url   {|row| $"($config.cache-url)/($row.name)"  }
  | upsert paths {|row| each {|it| $"($row.source)/($it)" } }
  | upsert file  {|row| [ $deps $row.name ] | path join     }

  for $sysroot in $sysroots {
    download $sysroot.url $sysroot.sha256 $sysroot.file
  }

  $sysroots 
  | par-each {|sysroot| 
      extract $sysroot.file --directory=($directory) --strip-components=1 ...($sysroot.paths)
    }

  $config.sysroots.mappings?
  | compact
  | flatten
  | group-by --to-table target
  | upsert items { get sources | flatten }
  | each {|row| 
      cd $directory
      move $row.target ...($row.items) 
    }

  $config.sysroots.linker_scripts?
  | compact
  | flatten
  | wrap linker_script
  | upsert replacement {|row| $"/($row.linker_script | path dirname)/" }
  | par-each {|row|
      cd $directory
      open $row.linker_script 
      | str replace --all --regex "(/lib64/|/usr/lib64/|/usr/lib/|/lib/)" $row.replacement
      | save -f $row.linker_script
    }

  normalize-directory $directory

  $directory
}

def move [
    target: path,
    ...sources: path,
] {
    for source in $sources {
        let source = $source | path expand
        let directory = $source | path dirname 
        let pattern = $source | path join "**"

        let mapping = glob --no-dir $pattern 
        | path relative-to $directory
        | wrap relative
        | upsert source {|row| $directory | path join $row.relative }
        | upsert target {|row| $target | path join $row.relative }
        | select source target

        for row in $mapping {
            let source = $row.source
            let target = $row.target

            mkdir ($target | path dirname)
            mv $source $target
        }
    }
}

def paths-with-type [ ]: list<path> -> table<path: path, type: string> {
    wrap path
    | upsert type {|row| $row.path | path type }
}

def remove-empty-dirs [ ]: list<path> -> nothing {
    let paths = paths-with-type
    | where type == "dir"
    | upsert components {|row| $row.path | path split | length }
    | sort-by --reverse components

    for path in $paths {
        if (ls $path.path | is-empty) { rm $path.path }
    }
}

def get-target [ path: path ]: nothing -> string {
    ls -lD $path | first | get target
}

def relativize-symlinks [ ]: list<path> -> nothing {
    paths-with-type
    | where type == "symlink"
    | upsert target {|row| get-target $row.path | path basename }
    | upsert directory {|row| $row.path | path dirname }
    | upsert file {|row| $row.path | path basename }
    | par-each {|row|
        cd $row.directory
        rm $row.file
        ln -s $row.target $row.file
      }
    
    null
}

def extract [
  file: path,
  ...paths: string,
  --directory: path = ".",
  --strip-components: int = 0,
]: nothing -> nothing {
  mkdir $directory
  (
    ^bsdtar
      --no-same-owner
      --no-same-permissions
      --directory=($directory)
      --no-same-owner
      --no-same-permissions
      --strip-components=($strip_components)
      --modification-time
      --extract
      --gzip
      --file=($file)
      ...($paths)
  )
}

def download [
  url: string,
  sha256: string,
  file: path,
  --force,
]: nothing -> nothing {
  if $force or not ($file | path exists) {
    mkdir ($file | path dirname)
    http get $url | save --progress --force=($force) $file
  }  
  
  verify $file $sha256
}

def verify [
  file: path,
  sha256: string,
]: nothing -> nothing {
  let actual_sha256 = open $file | hash sha256

  if $actual_sha256 != $sha256 {
    error make --unspanned { msg: $"sha256 mismatch: wanted ($sha256), got ($actual_sha256)" }
  }
}

def verify-dir [
  directory: path,
  sha256: string,
]: nothing -> nothing {
  let paths = glob --no-dir $"($directory)/**" 
  | path relative-to $directory
  | sort

  let checksums = $paths | par-each --keep-order { open | hash md5 }

  let actual_sha256 = $checksums | to text | hash sha256

  if $actual_sha256 != $sha256 {
    error make --unspanned { msg: $"sha256 mismatch: wanted ($sha256), got ($actual_sha256)" }
  }
}

def semver-to-int [version: string]: nothing -> int {
  let parts = ($version | split row "." | into int)
  ($parts.0 * 1_000_000) + ($parts.1 * 10_000) + ($parts.2 * 100)
}