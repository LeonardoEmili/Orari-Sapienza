# Orari Sapienza
Check course timetables and study rooms availability at Sapienza using a phone app!

## Data authenticity
In-app timetables are retrieved either in clean format from publicy available APIs (e.g. [corsidilaurea](https://corsidilaurea.uniroma1.it/it/), [studiareinformatica](https://www.studiareinformatica.uniroma1.it/)) or scraped from files whenever necessary (e.g. timetables in PDF format). The data itself is not shipped with the app and can be only be accessed by the application through authentication keys. To enable offline usage, it is securely stored as an encrypted SQLite Database on your phone.

## Where to find it
~~You may find the app at this [link](https://play.google.com/store/apps/details?id=com.sterbsociety.orarisapienza) on the Google Play Store.~~ Since this project is no longer mainted by us, an old version of the app can be found [here](https://github.com/LeonardoEmili/Orari-Sapienza/blob/master/app.apk) (outdated). [[NEWS](https://www.uniroma1.it/it/notizia/nuove-app-gli-studenti)] Sapienza has now released its official apps, we encourage you to download them for [Android](https://play.google.com/store/apps/details?id=sapienza.informatica.infostud) and iOS.


## More on the app
We use [SQLite](https://www.sqlite.org/index.html) to securely store databases on your phone, [Firebase Realtime Database](https://firebase.google.com/docs/database) as NOSQL database at server-side, [aFileChooser](https://github.com/iPaulPro/aFileChooser) to pick files from external storage.

## Authors
- [Leonardo Emili](https://github.com/LeonardoEmili)
- [Fabio Capirchio](https://github.com/fabiocapi)