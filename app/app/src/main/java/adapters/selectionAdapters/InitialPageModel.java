package adapters.selectionAdapters;

/**
 * Created by Fechner on 2/27/15.
 */
public class InitialPageModel implements GeneralRowInterface{

    public String title;
    public String childIdentifier;

    public InitialPageModel(String title, String childIdentifier) {
        this.title = title;
        this.childIdentifier = childIdentifier;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getChildIdentifier() {
        return childIdentifier;
    }
}
