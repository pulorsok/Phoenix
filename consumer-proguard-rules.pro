# keep getters/setters in RotatingDrawable so that animations can still work.
-keepclassmembers class toan.android.floatingactionmenu.FloatingActionsMenu$RotatingDrawable {
   void set*(***);
   *** get*();
}
