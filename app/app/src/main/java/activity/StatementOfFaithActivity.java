package activity;

import android.os.Bundle;

import org.unfoldingword.mobile.R;

import fragments.StatementOfFaithFragment;

public class StatementOfFaithActivity extends UWBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_only_fragment);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new StatementOfFaithFragment())
                    .commit();
        }
        setupToolbar(true, "Statement Of Faith", false);
    }

    @Override
    public AnimationParadigm getAnimationParadigm() {
        return AnimationParadigm.ANIMATION_VERTICAL;
    }
}
