package ml.karmaconfigs.api.bungee.karmayaml;

import java.io.File;

/**
 GNU LESSER GENERAL PUBLIC LICENSE
 Version 2.1, February 1999

 Copyright (C) 1991, 1999 Free Software Foundation, Inc.
 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 Everyone is permitted to copy and distribute verbatim copies
 of this license document, but changing it is not allowed.

 [This is the first released version of the Lesser GPL.  It also counts
 as the successor of the GNU Library Public License, version 2, hence
 the version number 2.1.]
 */
final class NotYamlError extends Error {

    /**
     * Initialize the exception
     *
     * @param file the supposed yaml file
     */
    public NotYamlError(File file) {
        super("The file " + file.getName() + " is not a yaml (.yml) file");
    }
}
