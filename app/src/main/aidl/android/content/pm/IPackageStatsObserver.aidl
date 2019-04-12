// IPackageStatsObserver.aidl
package android.content.pm;
import android.content.pm.PackageStats;
// Declare any non-default types here with import statements

interface IPackageStatsObserver {
    oneway void onGetStatsCompleted(in PackageStats pStats, boolean succeeded);
}
