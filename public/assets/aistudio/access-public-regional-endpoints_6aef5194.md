# גישה לממשקי Google APIs אזוריים דרך נקודות קצה ציבוריות &nbsp;|&nbsp; Security &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/security/compliance/access-public-regional-endpoints

אפשר לגשת לנקודות קצה אזוריות ורב-אזוריות באופן ציבורי דרך האינטרנט או באופן פרטי מתוך הענן הווירטואלי הפרטי (VPC). במאמר הזה מוסבר איך לגשת לנקודות הקצה באופן ציבורי.

כשניגשים לנקודות קצה אזוריות מהאינטרנט הציבורי, תעבורת הנתונים מנותבת דרך Google Cloud [המסלול הרגיל של רשתות](https://docs.cloud.google.com/network-tiers/docs/overview?hl=he). החיבור, כולל סיום ה-TLS, מטופל באזור היעד.

הגדרת גישה לאינטרנט הציבורי לממשקי ה-API
----------------------------------------

כדי להגדיר גישה לאינטרנט הציבורי לנקודות קצה אזוריות של API, צריך לבצע את השלבים הבאים.

### תפקידים

כדי לקבל את ההרשאות שדרושות ליצירת נקודת קצה אזורית, צריך לבקש מהאדמין להקצות לכם את תפקידי ה-IAM הבאים ברשת ה-VPC:

*   [התפקיד 'אדמין של רשתות ב-Compute'](https://docs.cloud.google.com/iam/docs/roles-permissions/compute?hl=he#compute.networkAdmin) (`roles/compute.networkAdmin`)
*   [התפקיד אדמין DNS](https://docs.cloud.google.com/iam/docs/roles-permissions/dns?hl=he#dns.admin) (`roles/dns.admin`)

להסבר על מתן תפקידים, ראו [איך מנהלים את הגישה ברמת הפרויקט, התיקייה והארגון](https://docs.cloud.google.com/iam/docs/granting-changing-revoking-access?hl=he).

יכול להיות שאפשר לקבל את ההרשאות הנדרשות גם באמצעות [תפקידים בהתאמה אישית](https://docs.cloud.google.com/iam/docs/creating-custom-roles?hl=he) או [תפקידים מוגדרים מראש](https://docs.cloud.google.com/iam/docs/roles-overview?hl=he#predefined).

### לפני שמתחילים

1.  [התקינו](https://docs.cloud.google.com/sdk/docs/install?hl=he) את ה-CLI של Google Cloud. אחר כך, [מאתחלים](https://docs.cloud.google.com/sdk/docs/initializing?hl=he) את ה-CLI של Google Cloud באמצעות הפקודה הבאה:
    
    $ gcloud init
    
    אם אתם משתמשים בספק זהויות חיצוני (IdP), קודם אתם צריכים [להיכנס ל-CLI של gcloud באמצעות המאגר המאוחד לניהול זהויות](https://docs.cloud.google.com/iam/docs/workforce-log-in-gcloud?hl=he).
    
2.  מפעילים את ממשקי ה-API של Compute Engine,‏ Network Connectivity Center ו-Cloud DNS, אם אחד מהם עדיין לא מופעל.
    
    **תפקידים שנדרשים להפעלת ממשקי API**
    
    כדי להפעיל ממשקי API, צריך את ההרשאה `serviceusage.services.enable`. אם יצרתם את הפרויקט, סביר להניח שכבר יש לכם את ההרשאה הזו דרך התפקיד 'בעלים' (`roles/owner`). אחרת, תוכלו לקבל את ההרשאה הזו דרך התפקיד 'אדמין בממשק Service Usage' (`roles/serviceusage.serviceUsageAdmin`). [איך מקצים תפקידים](https://docs.cloud.google.com/iam/docs/granting-changing-revoking-access?hl=he)
    
    [הפעלת ממשקי ה-API](https://console.cloud.google.com/apis/enableflow?apiid=compute.googleapis.com%2Cnetworkconnectivity.googleapis.com%2Cdns.googleapis.com&hl=he)
    
3.  בנוסף, צריך [להפעיל את ה-API](https://docs.cloud.google.com/apis/docs/getting-started?hl=he#enabling_apis) בשירות היעד שאליו רוצים לגשת דרך נקודת הקצה. לדוגמה, אם רוצים לגשת אל `spanner.me-central2.rep.googleapis.com`, צריך להפעיל את Cloud Spanner API.
    
4.  מוודאים שכללי חומת האש ליציאה מאפשרים תעבורת נתונים לנקודת הקצה. הגדרת ברירת המחדל של חומת האש לרשת VPC מאפשרת את התנועה הזו כי היא כוללת כלל מרומז שמאפשר תעבורת נתונים יוצאת. מוודאים שלא יצרתם כלל יציאה בעדיפות גבוהה יותר שחוסם את התנועה.
    

### זיהוי נקודת הקצה

קובעים את השירות ואת האזור הספציפי או את מספר האזורים שנדרשים לעומס העבודה.

*   נקודות קצה אזוריות הן בפורמט הבא: `SERVICE.REGION.rep.googleapis.com`
    
*   נקודות קצה עם מספר אזורים הן בפורמט הבא: `SERVICE.MULTI_REGION.rep.googleapis.com`
    

### שינוי הגדרת ה-DNS

אם הסביבה שלכם מבטלת את ה-DNS הציבורי עבור `googleapis.com` באמצעות אזור DNS פרטי, שזה דבר נפוץ בהגדרות היברידיות, צריך לבצע את השלבים הבאים:

*   מעדכנים את הגדרות ה-DNS כדי להחריג את `rep.googleapis.com`. מידע נוסף זמין במאמר [הוספה, עדכון ומחיקה של רשומות](https://docs.cloud.google.com/dns/docs/records?hl=he).
    
*   משנים את הגדרת תחום ה-DNS כדי להחריג את תת-הדומיין `rep.googleapis.com`. העברת השאילתות האלה לפתרון DNS ציבורי. מידע נוסף מופיע במאמר בנושא [יצירה, שינוי ומחיקה של אזורים](https://docs.cloud.google.com/dns/docs/zones?hl=he).
    

### הגדרת Google Cloud CLI

כדי לשנות את נקודת הקצה ל-API שמוגדרת כברירת מחדל לשירותים ספציפיים, משתמשים ב-Google Cloud CLI. בדוגמה הבאה משתמשים בפקודה [`gcloud config set`](https://docs.cloud.google.com/sdk/gcloud/reference/config/set?hl=he) כדי להגדיר שינוי זמני ל-Cloud Storage ב-`us-central1` ושינוי זמני ל-Compute Engine ב-`europe-west1`.

# Set an override for Cloud Storage in us-central1.
    
    gcloud config set api_endpoint_overrides/storage https://storage.us-central1.rep.googleapis.com/
    
    # Set an override for Compute Engine in europe-west1.
    
    gcloud config set api_endpoint_overrides/compute https://compute.europe-west1.rep.googleapis.com/compute/v1/

אחרי שמגדירים את החלפת ברירת המחדל, פקודות ה-CLI של gcloud עבור השירות הזה משתמשות בנקודת הקצה האזורית. כדי לחזור לשימוש בנקודת הקצה הגלובלית שמוגדרת כברירת מחדל, משתמשים בפקודה [`gcloud config unset`](https://docs.cloud.google.com/sdk/gcloud/reference/config/unset?hl=he):

gcloud config unset api_endpoint_overrides/SERVICE

### עדכון של ערכות ה-SDK של הלקוח

ברוב Google Cloud ספריות הלקוח אפשר לציין נקודת קצה מותאמת אישית במהלך אתחול הלקוח.

### Python

שימוש ב-`client_options` עם `api_endpoint`.

from google.cloud import SERVICE
    from google.api_core.client_options import ClientOptions
    
    options = ClientOptions(api_endpoint='https://SERVICE.REGION.rep.googleapis.com')
    SERVICE_client = SERVICE.Client(client_options=options, project='PROJECT_ID')
    # ... use SERVICE_client as usual

מחליפים את מה שכתוב בשדות הבאים:

*   ‫`SERVICE`: השם של השירות
*   ‫`REGION`: האזור של נקודת הקצה
*   ‫`PROJECT_ID`: מזהה הפרויקט ב- Google Cloud

### המשך

משתמשים ב-`option.WithEndpoint` במהלך יצירת הלקוח.

client, err := SERVICE.NewClient(ctx, projID, 
    option.WithEndpoint("https://SERVICE-my-endpoint.REGION.rep.googleapis.com"), // Override endpoint

מחליפים את מה שכתוב בשדות הבאים:

*   ‫`SERVICE`: השם של השירות
*   ‫`REGION`: האזור של נקודת הקצה

### ‎.NET

מגדירים את `Endpoint` בכלי ליצירת לקוחות.

### Java

משתמשים ב-`setEndpoint` בכלי ליצירת הגדרות שירות.

בדוגמה הבאה נעשה שימוש ב-Java כדי להתחבר לנקודת הקצה של Cloud Vision API.

ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
    .setEndpoint("https://vision.REGION.rep.googleapis.com")
    .build();
    ImageAnnotatorClient client = ImageAnnotatorClient.create(settings);

מאמרי עזרה
----------

*   משתמשים ב-[Google Cloud CLI כדי לנהל נקודות קצה אזוריות](https://docs.cloud.google.com/sdk/gcloud/reference/network-connectivity/regional-endpoints?hl=he).
*   [לעיון במאמרי העזרה של `regionalEndpoints` REST](https://docs.cloud.google.com/network-connectivity/docs/reference/networkconnectivity/rest/v1/projects.locations.regionalEndpoints?hl=he)

המאמרים הבאים
-------------

*   [גישה לממשקי Google APIs אזוריים דרך בק-אנדים](https://docs.cloud.google.com/vpc/docs/configure-private-service-connect-controls?hl=he).
*   Google Cloud [כאן מפורטות נקודות הקצה האזוריות הנתמכות](https://docs.cloud.google.com/docs/security/compliance/regional-service-endpoints?hl=he).
