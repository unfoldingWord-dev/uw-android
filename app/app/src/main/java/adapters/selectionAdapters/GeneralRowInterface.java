/**
 * Copyright (c) 2015 unfoldingWord
 * http://creativecommons.org/licenses/MIT/
 * See LICENSE file for details.
 * Contributors:
 * PJ Fechner <pj@actsmedia.com>
 */

package adapters.selectionAdapters;

/**
 * Created by Fechner on 2/27/15.
 */
public interface GeneralRowInterface {

    String getTitle();
    String getChildIdentifier();

    class BasicGeneralRowInterface implements GeneralRowInterface{

        private String childIdentifier;
        private String title;

        public BasicGeneralRowInterface(String childIdentifier, String title) {
            this.childIdentifier = childIdentifier;
            this.title = title;
        }

        @Override
        public String getChildIdentifier() {
            return childIdentifier;
        }

        @Override
        public String getTitle() {
            return title;
        }
    }
}
