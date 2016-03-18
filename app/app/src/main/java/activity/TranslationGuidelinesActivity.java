package activity;

import android.os.Bundle;

import org.unfoldingword.mobile.R;

import fragments.StatementOfFaithFragment;
import fragments.TranslationGuidelinesFragment;

public class TranslationGuidelinesActivity extends UWBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_only_fragment);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new TranslationGuidelinesFragment())
                    .commit();
        }
        setupToolbar(true, "Translation Guidelines", false);
    }

    @Override
    public AnimationParadigm getAnimationParadigm() {
        return AnimationParadigm.ANIMATION_VERTICAL;
    }
}
