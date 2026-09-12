# App Review Information — notes for the reviewer

Paste the block below into **App Review Information → Notes** in App Store Connect. Keep it in
English. Attach `docs/store/play/demo-data/leshy-demo-en.zip` in the **Attachment** field of the
same section — without it the app looks empty to anyone reviewing it indoors.

Sign-in is not required: the app has no accounts, so the demo-account fields stay empty.

---

Mushroom Map from Leshy is an offline logbook for mushroom foraging: it records the walk (GPS
track), the finds you mark along the way, photos and landmarks, and later shows all of your
seasons on one map with statistics. No account, no sign-up, no ads, no analytics. Everything is
stored on the device; the only network request the app makes is for map tiles (OpenFreeMap).

HOW TO SEE THE APP WITH DATA, INDOORS

Recording a real walk requires walking outdoors, so a demo archive is attached. To load it:
side menu (button at the top left) -> "Export/Import" -> "Import" -> "Choose file" -> pick
leshy-demo-en.zip -> "Done". It adds six finished walks with tracks, finds, photos and marked
places. After that, "Walk Archive" and "Finds Map" are fully populated, including the statistics
and the per-species charts.

LOCATION AND BACKGROUND MODE

The app asks for When In Use location only, and only on the recording screen. During an active
recording it enables background location updates (UIBackgroundModes: location) for one reason:
a walk lasts hours, the phone goes into a pocket with the screen locked, and the track must keep
being written. Recording is always started explicitly by the user and is visible while it runs;
outside an active recording the app collects no location at all. There is no background
collection when the user is not recording a walk.

Camera access is used to photograph a find or a landmark from inside the app. Photos picked from
the library go through PHPickerViewController, so no photo library permission is requested.

WHAT THE APP DELIBERATELY DOES NOT DO

It does not identify mushrooms and does not tell edible species from poisonous ones. The species
catalogue is a list to mark your own finds against; the pictures are illustrations, not an
identification key. This is stated in the app itself (onboarding and the species screens) and in
the App Store description.

PRIVACY

No data is collected. Nothing is sent to the developer or to any third party. Privacy policy:
https://leshy-mapper.github.io/mushrooms-map/privacy.html
Support: https://leshy-mapper.github.io/mushrooms-map/support.html
