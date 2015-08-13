package fragments;

import model.daoModels.Version;

/**
 * Created by Fechner on 7/26/15.
 */
public interface ReadingFragmentListener {

    void toggleNavBar();
    void showCheckingLevel(Version version);
    void clickedChooseVersion(boolean isSecondReadingView);
}
