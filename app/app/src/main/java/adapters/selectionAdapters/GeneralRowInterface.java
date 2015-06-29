package adapters.selectionAdapters;

/**
 * Created by Fechner on 2/27/15.
 */
public interface GeneralRowInterface {

    public String getTitle();
    public String getChildIdentifier();

    public class BasicGeneralRowInterface implements GeneralRowInterface{

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
