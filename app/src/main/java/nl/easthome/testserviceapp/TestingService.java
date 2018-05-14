package nl.easthome.testserviceapp;
import android.content.Intent;
import android.support.annotation.NonNull;

public class TestingService extends AndroidJobIntentServiceCombinator<TestingService> {

    public TestingService() {

    }

    @Override
    protected void handleIntent(@NonNull Intent intent) {
        System.out.println(intent);
    }

    @Override
    protected TestingService returnOwnThis() {
        return this;
    }
}
