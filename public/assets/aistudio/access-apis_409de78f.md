# Set up API access &nbsp;|&nbsp; Get started &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/get-started/access-apis

Google Cloud APIs help you programmatically access Google Cloud services from the command line, through automated scripts, or in your own applications.

For example, you might want to develop an application that helps administrators analyze how their resources are utilized across multiple cloud providers. To do this, you need to access log data from your Google Cloud resources.

To set up API access, implement the following:

*   [Google Cloud APIs: Access services programmatically](#apis)
*   [Cloud Client Libraries: Access APIs with your preferred language](#libraries)

Before you begin
----------------

To make sure you can set up APIs and use tools, ask your administrators to complete the following tasks:

*   Create an account that you use to sign in and use Google Cloud products, including Google Cloud console and Google Cloud CLI.
*   Create a project that serves as an access boundary for your Google Cloud resources.
*   Enable billing on your project so you can pay for service and API usage.

For detailed instructions to complete setup steps, see [Google Cloud Setup guided flow](https://docs.cloud.google.com/docs/enterprise/cloud-setup).

Google Cloud APIs: Access services programmatically
---------------------------------------------------

Google Cloud APIs are programmatic interfaces to Google Cloud services. You can use APIs to access computing, networking, storage, and other services. For example, you might create a resource utilization application that pulls log data from your Google Cloud resources. To retrieve the required data, you use the Cloud Logging API.

You can access Google Cloud APIs using REST calls or client libraries. We recommend that you use client libraries, which are available for many popular programming languages. You can also access Cloud APIs with the Google Cloud CLI tools or Google Cloud console.

For steps to enable an API, see [Getting started](https://docs.cloud.google.com/apis/docs/getting-started) in the Cloud APIs documentation.

Cloud Client Libraries: Access APIs with your preferred language
----------------------------------------------------------------

Cloud Client Libraries help you access Google Cloud APIs from a supported language of your choice. Each library supports your preferred language conventions and simplifies the code that you write in your application. The client libraries can handle common API processes, including authentication, error handling, retry, and payload validation. For example, if your preferred development language is Java, you might use the Cloud Logging with Java library.

To choose and install a library, see [Cloud Client Libraries](https://docs.cloud.google.com/apis/docs/cloud-client-libraries).

What's next?
------------

*   [Understand authentication in Google Cloud](https://docs.cloud.google.com/docs/get-started/authentication)
    
*   [Google Cloud samples](https://docs.cloud.google.com/docs/samples)
