package no.erlyberly.bootlegterraria;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;

public class IOSLauncher extends IOSApplication.Delegate {

    private static String[] args;

    @Override
    protected IOSApplication createApplication() {
        final IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        return new IOSApplication(new GameMain(args), config);
    }

    public static void main(final String[] argv) {
        args = argv;
        final NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IOSLauncher.class);
        pool.close();
    }
}