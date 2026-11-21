# Building a Go application &nbsp;|&nbsp; Buildpacks &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/buildpacks/go

Specifying the Go Version
-------------------------

By default the Go buildpack uses the latest stable version of the Go compiler. If your application requires a specific version, you can use the `GOOGLE_GO_VERSION` environment variable to provide a semver constraint that will be used to select an available Go version.

pack build sample-go --builder=gcr.io/buildpacks/builder \
      --env GOOGLE_GO_VERSION="17.x.x"

Compilation Flags
-----------------

The Go buildpack compiles your application source into an executable using the `go build command`. The following environment variables can be used to configure the build behavior:

*   `GOOGLE_BUILDABLE` Specifies path to a buildable unit. Example: `./maindir` for Go will build the package rooted at `maindir`.
    
*   `GOOGLE_CLEAR_SOURCE` Omit the source code from the application image. If the application depends on static files, such as Go templates, setting this variable may cause the application to misbehave. Example: `true`, `True`, `1` will clear the source.
    
*   `GOOGLE_GOGCFLAGS` Passed to `go build` and `go run` as `-gcflags` value with no interpretation. Example: `all=-N -l` enables race condition analysis and changes how source filepaths are recorded in the binary.
    
*   `GOOGLE_GOLDFLAGS` Passed to go build and go run as `-ldflags` value with no interpretation. Example: `-s -w` is used to strip and reduce binary size.
    

Managing Dependencies
---------------------

We recommend that you use [Go modules](https://go.dev/blog/using-go-modules) to manage dependencies in your Go app. The Go buildpack uses the `go build` command to build your app and therefore matches the behavior of Go itself. To ensure that your app uses module-aware mode, you should include a `go.mod` file in your application root.

### Vendoring dependencies

Vendoring copies the packages your app uses into the application directory instead of downloading modules from their sources during the build process. Go provides the go build command [to vendor the packages your app needs](https://pkg.go.dev/cmd/go#hdr-Modules_and_vendoring) into a directory named `vendor` in your app's root directory.

Configure the Application Entrypoint
------------------------------------

By default, the Go buildpack will configure the application container entry invoke the `main` executable produced when compiling your application source. If you need to override this, you can do so by providing a `Procfile` or passing the `GOOGLE_ENTRYPOINT` environment variable.

Environment Variables
---------------------

The Go buildpack supports the following environment variables to customize your container

### GO

See Go documentation on [Environment Variables](https://pkg.go.dev/cmd/go#hdr-Environment_variables)

**Example:** `GOFLAGS=-flag=value` passes `-flag=value` to `go` commands.
