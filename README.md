
[![build status](https://gitlab.com/bitfireAT/cert4android/badges/master/build.svg)](https://gitlab.com/bitfireAT/cert4android/commits/master)


# cert4android

cert4android is an Android library for managing custom certificates which has
been developed for [DAVdroid](https://davdroid.bitfire.at). Feel free to use
it in your own open-source app.

Discussion: https://forums.bitfire.at/category/7/transport-level-security


# Features

* uses a service to manage custom certificates
* supports multiple threads and multiple processes (for instance, if you have an UI
  and a separate `:sync` process which should share the certificate information)


# How to use

1. Clone cert4android as a submodule.
1. Add the submodule to `settings.gradle` / `app/build.gradle`.
1. Create an instance of `CustomCertManager` (`Context` is required to connect to the
   `CustomCertService`, which manages the custom certificates).
1. Use this instance as `X509TrustManager` in your calls (for instance, when setting up your HTTP client).
   Don't forget to get and use the `hostnameVerifier()`, too.
1. Close the instance when it's not required anymore (will disconnect from the
   `CustomCertService`, thus allowing it to be destroyed).

You can overwrite resources when you want, just have a look at the `res/strings`
directory. Especially `certificate_notification_connection_security` and
`trust_certificate_unknown_certificate_found` should contain your app name.


# License 

Copyright (C) bitfire web engineering (Ricki Hirner, Bernhard Stockmann).

This program comes with ABSOLUTELY NO WARRANTY. This is free software, and you are welcome
to redistribute it under the conditions of the [GNU GPL v3](LICENSE).

