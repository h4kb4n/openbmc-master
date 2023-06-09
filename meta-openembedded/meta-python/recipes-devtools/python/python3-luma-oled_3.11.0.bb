SUMMARY = "A small library to drive an OLED device"
DESCRIPTION = "\
A small library to drive an OLED device with either SSD1306 , SSD1309, SSD1322, \
SSD1325, SSD1327, SSD1331, SSD1351 or SH1106 chipset"
HOMEPAGE = "https://github.com/rm-hull/luma.oled"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=5ec447eb69733e20a55838de7e8cb991"

inherit pypi setuptools3

SRC_URI[sha256sum] = "81583e77ecb1d3ae04a641b3311082719b7a3149c15a5f2577d9b93b243e9331"

CLEANBROKEN = "1"

PYPI_PACKAGE = "luma.oled"

RDEPENDS:${PN} += " \
	${PYTHON_PN}-luma-core \
"
