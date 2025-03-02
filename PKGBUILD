# Maintainer: @raz0229 https://github.com/raz0229
pkgname=pavalang
pkgver=1.0.0
pkgrel=1
pkgdesc="PavaLang Tree-walking interpreter"
arch=('any')
url="https://github.com/raz0229/pavalang/"
license=('MIT')
depends=('jdk-openjdk')
makedepends=('maven' 'git')
# For a release tarball you would include its URL here.
source=("git+https://github.com/raz0229/pavalang.git")
sha256sums=('SKIP')  # Use a proper hash if using a tarball source

build() {
  cd "$srcdir/pavalang"
  # Build the project with Maven
  mvn -e -B package -Ddir="$(dirname "$0")"
}

package() {
  cd "$srcdir/pavalang"
  # Create directory for the jar
  install -d "$pkgdir/usr/lib/pava"
  # Install the jar; adjust the path to the jar if necessary.
  install -m644 target/pava.jar "$pkgdir/usr/lib/pava/"

  # Create the wrapper script in /usr/bin so users can call pava directly.
  install -d "$pkgdir/usr/bin"
  cat << 'EOF' > "$pkgdir/usr/bin/pava"
#!/bin/sh
set -e
exec java -jar /usr/lib/pava/pava.jar "$@"
EOF
  chmod +x "$pkgdir/usr/bin/pava"
}
