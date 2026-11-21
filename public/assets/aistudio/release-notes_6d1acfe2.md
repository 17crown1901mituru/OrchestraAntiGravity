# Buildpacks release notes &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/buildpacks/release-notes?hl=ja

This page documents production updates to Buildpacks. Check this page for announcements about new or updated features, bug fixes, known issues, and deprecated functionality.

You can see the latest product updates for all of Google Cloud on the [Google Cloud](https://docs.cloud.google.com/release-notes) page, browse and filter all release notes in the [Google Cloud console](https://console.cloud.google.com/release-notes), or programmatically access release notes in [BigQuery](https://console.cloud.google.com/bigquery?p=bigquery-public-data&d=google_cloud_release_notes&t=release_notes&page=table).

To get the latest product updates delivered to you, add the URL of this page to your [feed reader](https://wikipedia.org/wiki/Comparison_of_feed_aggregators), or add the [feed URL](https://docs.cloud.google.com/feeds/cloudbuildpacks-release-notes.xml) directly.

October 24, 2026
----------------

Security

The following runtime languages contain important security updates:

*   [Go 1.24 and later](https://docs.cloud.google.com/docs/buildpacks/runtime-support#go): `1.24.13`, `1.25.14`, `1.26.10`, `1.27.3`
*   [Node.js 22 and later](https://docs.cloud.google.com/static/docs/buildpacks/runtime-support#node.js): `22.23.4`, `24.22.1`, `26.12.1`
*   [Java 17 and later](https://docs.cloud.google.com/docs/buildpacks/runtime-support#java): `17.0.20_101`, `21.0.12_101.0.LTS`, `25.0.5_8.0.LTS`
*   [PHP 8.2 and later](https://docs.cloud.google.com/docs/buildpacks/runtime-support#php): `8.2.35`, `8.3.36`, `8.4.27`, `8.5.12`
*   [Python 3.10 and later](https://docs.cloud.google.com/docs/buildpacks/runtime-support#python): `3.10.22`, `3.11.17`, `3.12.15`, `3.13.15`, `3.14.6`
*   [Ruby 3.3 and later](https://docs.cloud.google.com/docs/buildpacks/runtime-support#ruby): `3.3.12`, `3.4.11`, `4.0.7`
*   [.NET 6 and later](https://docs.cloud.google.com/docs/buildpacks/runtime-support#.net): `6.0.428`, `8.0.415`, `10.0.100`

The following stacks and their underlying operating system packages have also been updated:

*   `google-22`, `google-22-full`
*   `google-24`, `google-24-full`

Take immediate protective action by rebuilding and redeploying your resources with updated dependencies.

See [Security bulletins for Google Cloud](https://docs.cloud.google.com/support/bulletins) for more information.

October 21, 2026
----------------

Feature

Patch updates to [PHP runtimes](https://docs.cloud.google.com/appengine/docs/flexible/lifecycle/support-schedule#php) are available on `google-22` and `google-24` tags: `8.5.12`, `8.4.27`, `8.3.36`, and `8.2.35`. Take immediate action to protect your PHP workloads by rebuilding your workloads with updated dependencies. See [Responding to core PHP vulnerabilities](https://cloud.google.com/blog/products/identity-security/responding-to-core-php-vulnerabilities-cve-2026-109618-cve-2026-109619) for more information.

August 19, 2026
---------------

May 12, 2026
------------

April 03, 2026
--------------

March 13, 2026
--------------

February 17, 2026
-----------------

February 11, 2026
-----------------

January 22, 2026
----------------

January 13, 2026
----------------

December 19, 2025
-----------------

Feature

Cloud Run and Cloud Run functions source deployments support `pyproject.toml` file for managing dependencies. This feature is in General Availability for Python version 3.13 and later, and is in Preview for Python version 3.12 and earlier. For more information, see [Deploy Python applications with a `pyproject.toml` file](https://docs.cloud.google.com/docs/buildpacks/python#deploy-with-toml).

December 18, 2025
-----------------

November 14, 2025
-----------------

Feature

Cloud Run and Cloud Run functions source deployments support `pyproject.toml` file for managing dependencies. If you use a `pyproject.toml` file, source deployments use one of the following to find and install dependencies:

*   `pip`
*   `uv`
*   `poetry`

For more information, see [Deploy Python applications with a `pyproject.toml` file](https://docs.cloud.google.com/docs/buildpacks/python#deploy-with-toml) (Preview).

September 18, 2025
------------------

Feature

Ubuntu 24 builder with the `google-24` stack is available for Google Cloud's Buildpacks. For more information, see [Builders](https://docs.cloud.google.com/docs/buildpacks/builders) and [Use a specific builder](https://docs.cloud.google.com/docs/buildpacks/use-a-specific-builder).

August 14, 2025
---------------

September 23, 2024
------------------

February 26, 2024
-----------------

Feature

Starting in Go version 1.22 and later, you can no longer use `GOPATH` for installing dependencies. To manage dependencies, you use a [`go.mod`](https://cs.opensource.google/go/go/+/refs/tags/go1.22.0:src/go.mod) file. For more information about Go versions, and managing dependencies for vendor directories, see [GOPATH and Modules](https://pkg.go.dev/cmd/go#hdr-GOPATH_and_Modules) in Go documentation.

August 28, 2023
---------------

July 19, 2023
-------------

Change

The Python buildpack now uses `gunicorn --bind :8080 main:app` as the default entrypoint for all web applications. If you currently use a `Procfile`, it will continue to work but you are no longer required to configure a `Procfile` for your projects. Learn more about [Python application entrypoints](https://docs.cloud.google.com/docs/buildpacks/python#application_entrypoint).

June 27, 2023
-------------

Change

The default generic builder now uses the Ubuntu 22 LTS base image. When you specify the `latest` location, the builds now uses the `google-22` builder by default. For example, you can specify either of the following to use `google-22`:

*   `gcr.io/buildpacks/builder:latest`
*   `gcr.io/buildpacks/builder:google-22`

If you need to pin your build to the previous Ubuntu 18 builder, see the [instructions about how to configure the builder version](https://docs.cloud.google.com/docs/buildpacks/use-a-specific-builder), including:

*   `pack` commands for local builds.
*   `gcloud` commands for remote builds.
*   How to configure the `project.toml` for Cloud Run.

June 22, 2023
-------------

Feature

The Java runtime now supports using [Maven wrappers](https://docs.cloud.google.com/docs/buildpacks/java#configure_maven) for managing your project's dependency on Maven.

June 01, 2023
-------------

April 26, 2023
--------------

Announcement

Starting June 5, 2023, the default generic builder will begin using the Ubuntu 22 LTS base image. This means that builds using `gcr.io/buildpacks/builder:latest` will get the `google-22` builder which addresses multiple security issues. You can read more about the `google-22` builder in our [announcement on GitHub](https://docs.cloud.google.com/docs/buildpacks/github/buildpacks/discussions/271).

You can preview the new builder by adding `--builder=gcr.io/buildpacks/builder:google-22` to the `gcloud builds submit --pack` command when you build your application with a [specific builder](https://docs.cloud.google.com/docs/buildpacks/use-a-specific-builder).
