SUMMARY = "Hierarchical, reference counted memory pool system with destructors"
HOMEPAGE = "https://ldb.samba.org"
SECTION = "libs"
LICENSE = "LGPL-3.0-or-later & LGPL-2.1-or-later & GPL-3.0-or-later"

DEPENDS += "libtdb libtalloc libtevent popt"
RDEPENDS:pyldb += "python3"

export PYTHONHASHSEED="1"

SRC_URI = "http://samba.org/ftp/ldb/ldb-${PV}.tar.gz \
           file://0001-do-not-import-target-module-while-cross-compile.patch \
           file://0002-ldb-Add-configure-options-for-packages.patch \
           file://0003-Fix-pyext_PATTERN-for-cross-compilation.patch \
          "

SRC_URI:append:libc-musl = " file://cmocka-fix-musl-libc-conflicting-types-error.patch"

PACKAGECONFIG ??= "\
    ${@bb.utils.filter('DISTRO_FEATURES', 'acl', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'xattr', 'attr', '', d)} \
"
PACKAGECONFIG[acl] = "--with-acl,--without-acl,acl"
PACKAGECONFIG[attr] = "--with-attr,--without-attr,attr"
PACKAGECONFIG[ldap] = ",,openldap"
PACKAGECONFIG[libaio] = "--with-libaio,--without-libaio,libaio"
PACKAGECONFIG[libbsd] = "--with-libbsd,--without-libbsd,libbsd"
PACKAGECONFIG[libcap] = "--with-libcap,--without-libcap,libcap"
PACKAGECONFIG[valgrind] = "--with-valgrind,--without-valgrind,valgrind"
PACKAGECONFIG[lmdb] = ",--without-ldb-lmdb,lmdb,"

SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'ldap', '', 'file://avoid-openldap-unless-wanted.patch', d)}"

LIC_FILES_CHKSUM = "file://pyldb.h;endline=24;md5=dfbd238cecad76957f7f860fbe9adade \
                    file://man/ldb.3.xml;beginline=261;endline=262;md5=137f9fd61040c1505d1aa1019663fd08 \
                    file://tools/ldbdump.c;endline=19;md5=a7d4fc5d1f75676b49df491575a86a42"

SRC_URI[md5sum] = "e443a8da17758479c3b5c542e8e3ff1a"
SRC_URI[sha256sum] = "c4632c9a7f81f8a45ed46fc14d18eb507edf4e79f6e88d16977478ef95ed5b7f"

inherit pkgconfig waf-samba

S = "${WORKDIR}/ldb-${PV}"

#cross_compile cannot use preforked process, since fork process earlier than point subproces.popen
#to cross Popen
export WAF_NO_PREFORK="yes"

EXTRA_OECONF += "--disable-rpath \
                 --disable-rpath-install \
                 --bundled-libraries=cmocka \
                 --builtin-libraries=replace \
                 --with-modulesdir=${libdir}/ldb/modules \
                 --with-privatelibdir=${libdir}/ldb \
                 --with-libiconv=${STAGING_DIR_HOST}${prefix}\
                "

PACKAGES =+ "pyldb pyldb-dbg pyldb-dev"

NOAUTOPACKAGEDEBUG = "1"

FILES:${PN} += "${libdir}/ldb/*"
FILES:${PN}-dbg += "${bindir}/.debug/* \
                    ${libdir}/.debug/* \
                    ${libdir}/ldb/.debug/* \
                    ${libdir}/ldb/modules/ldb/.debug/*"

FILES:pyldb = "${libdir}/python${PYTHON_BASEVERSION}/site-packages/* \
               ${libdir}/libpyldb-util.*.so.* \
              "
FILES:pyldb-dbg = "${libdir}/python${PYTHON_BASEVERSION}/site-packages/.debug \
                   ${libdir}/.debug/libpyldb-util.*.so.*"
FILES:pyldb-dev = "${libdir}/libpyldb-util.*.so"

# Prevent third_party/waf/waflib/Configure.py checking host's path which is
# incorrect for cross building.
export PREFIX = "/"
export LIBDIR = "${libdir}"
export BINDIR = "${bindir}"

do_configure:prepend() {
    # For a clean rebuild
    rm -fr bin/
}
