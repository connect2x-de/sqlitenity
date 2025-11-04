#!/usr/bin/env nu

use util

let sysroot = util download-sysroot --force
let ndk = util download-ndk --force

let llvm  = brew --prefix llvm
let lld   = brew --prefix lld
let ninja = brew --prefix ninja

let path = [ $llvm $lld $ninja ] 
| each { path join bin }
| append $env.PATH

with-env {
  PATH: $path
  DESKTOP_SYSROOT: $sysroot
  ANDROID_NDK_HOME: $ndk
} {
  ninja
}

# let urlbase = $"($env.CI_API_V4_URL)/projects/($env.CI_PROJECT_ID)/packages/generic/libsqlitenity/($env.CI_COMMIT_SHA)"
#
# glob "{lib,archive}/**/*.{a,so,dll,dylib}" 
# | wrap file 
# | insert filename { get file | path split | last 2 | path join }
# | insert url { $"($urlbase)/($in.filename)" }
# | select file url
# | each {|row|
#     open --raw $in.file | http put --headers { JOB-TOKEN: $env.CI_JOB_TOKEN } $row.url
#   }
