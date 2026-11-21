# Set up ADC for on-premises or another cloud provider &nbsp;|&nbsp; Authentication &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/authentication/set-up-adc-on-premises

If you are running your application outside of Google Cloud, you need to provide credentials that are recognized by Google Cloud to use Google Cloud services.

### Workload Identity Federation

The preferred way to authenticate with Google Cloud using credentials from an external IdP is to use [Workload Identity Federation](https://docs.cloud.google.com/iam/docs/workload-identity-federation); you create a credential configuration file and set the `GOOGLE_APPLICATION_CREDENTIALS` environment variable to point to it. This approach is more secure than creating a service account key.

For help with setting up Workload Identity Federation for ADC, see [Workload Identity Federation with other clouds](https://docs.cloud.google.com/iam/docs/workload-identity-federation-with-other-clouds).

### Service account key

If you are not able to configure Workload Identity Federation, then you must create a service account, grant it the IAM roles that your application requires, and create a key for the service account.

To create a service account key and make it available to ADC:

1.  Create a service account with the roles your application needs, and a key for that service account, by following the instructions in [Creating a service account key](https://docs.cloud.google.com/iam/docs/keys-create-delete#creating).
2.  Set the environment variable `GOOGLE_APPLICATION_CREDENTIALS` to the path of the JSON file that contains your credentials. This variable applies only to your current shell session, so if you open a new session, set the variable again.
    
    **Example:** Linux or macOS
    
    export GOOGLE\_APPLICATION\_CREDENTIALS\="`KEY_PATH`"
    
    Replace `KEY_PATH` with the path of the JSON file that contains your credentials.
    
    For example:
    
    export GOOGLE\_APPLICATION\_CREDENTIALS\="/home/user/Downloads/service-account-file.json"
    
    **Example:** Windows
    
    For PowerShell:
    
    $env:GOOGLE\_APPLICATION\_CREDENTIALS\="`KEY_PATH`"
    
    Replace `KEY_PATH` with the path of the JSON file that contains your credentials.
    
    For example:
    
    $env:GOOGLE\_APPLICATION\_CREDENTIALS\="C:\\Users\\username\\Downloads\\service-account-file.json"
    
    For command prompt:
    
    set GOOGLE\_APPLICATION\_CREDENTIALS\=`KEY_PATH`
    
    Replace `KEY_PATH` with the path of the JSON file that contains your credentials.
    

What's next
-----------

*   Learn about [Workload Identity Federation](https://docs.cloud.google.com/iam/docs/workload-identity-federation).
*   Understand best practices for using [service account keys](https://docs.cloud.google.com/iam/docs/best-practices-for-managing-service-account-keys).
*   Learn more about [how ADC finds credentials](https://docs.cloud.google.com/docs/authentication/application-default-credentials).
*   Explore [authentication methods](https://docs.cloud.google.com/docs/authentication).
