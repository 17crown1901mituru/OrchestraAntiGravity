# הגדרת ADC למשאב עם חשבון שירות מצורף &nbsp;|&nbsp; Authentication &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/authentication/set-up-adc-attached-service-account

בחלק Google Cloud מהשירותים – כמו Compute Engine,‏ App Engine ופונקציות של Cloud Run – אפשר לצרף לסוגים מסוימים של משאבים [חשבונות שירות שמשתמשים מנהלים](https://docs.cloud.google.com/iam/docs/service-account-types?hl=he#user-created). באופן כללי, אפשר לצרף חשבון שירות למשאבים של השירות שיכולים להריץ או לכלול קוד של האפליקציה. כשמחברים חשבון שירות למשאב, הקוד שרץ במשאב יכול להשתמש בחשבון השירות הזה בתור הזהות שלו.

צירוף של חשבון שירות שמשתמש מנהל היא הדרך המועדפת להעברת פרטי הכניסה ל-ADC, בקוד שרץ ב- Google Cloud.

במאמר [בחירת תפקידים מוגדרים מראש](https://docs.cloud.google.com/iam/docs/choose-predefined-roles?hl=he) מוסבר איך מגדירים ב-IAM את התפקידים הנדרשים לחשבון השירות.

ב[מאמרים ומסמכי העזרה של IAM בקשר לצירוף חשבונות שירות](https://docs.cloud.google.com/iam/docs/attach-service-accounts?hl=he#attaching-new-resource) מוסבר לאילו משאבים אפשר לצרף חשבונות שירות ואיך מצרפים אותם.

מגדירים את האימות:

1.  מוודאים שיש לכם את תפקיד ה-IAM ‏Create Service Accounts ‏(`roles/iam.serviceAccountCreator`) ואת תפקיד ה-IAM ‏Project Admin ‏(`roles/resourcemanager.projectIamAdmin`). [איך מקצים תפקידים](https://docs.cloud.google.com/iam/docs/granting-changing-revoking-access?hl=he)
2.  יוצרים את חשבון השירות:
    
    gcloud iam service-accounts create SERVICE\_ACCOUNT\_NAME
    
    מחליפים את הערך `SERVICE_ACCOUNT_NAME` בשם שרוצים לתת לחשבון השירות.
    
3.  כדי לתת גישה לפרויקט ולמשאבים, מקצים תפקיד לחשבון השירות:
    
    gcloud projects add-iam-policy-binding PROJECT\_ID \--member\="serviceAccount:SERVICE\_ACCOUNT\_NAME@PROJECT\_ID.iam.gserviceaccount.com" \--role\=ROLE
    
    מחליפים את מה שכתוב בשדות הבאים:
    
    *   `SERVICE_ACCOUNT_NAME`: השם של חשבון השירות
    *   `PROJECT_ID`: מזהה הפרויקט שבו יצרתם את חשבון השירות
    *   `ROLE`: התפקיד שאתם רוצים לתת
4.  כדי להקצות תפקיד נוסף לחשבון השירות, מריצים שוב את הפקודה שמו שעשיתם בשלב הקודם.
5.  מקצים את התפקיד הנדרש לחשבון המשתמש שיצרף את חשבון השירות למשאבים אחרים.
    
    gcloud iam service-accounts add-iam-policy-binding SERVICE\_ACCOUNT\_NAME@PROJECT\_ID.iam.gserviceaccount.com \--member\="user:USER\_EMAIL" \--role\=roles/iam.serviceAccountUser
    
    מחליפים את מה שכתוב בשדות הבאים:
    
    *   `SERVICE_ACCOUNT_NAME`: השם של חשבון השירות
    *   `PROJECT_ID`: מזהה הפרויקט שבו יצרתם את חשבון השירות
    *   `USER_EMAIL`: כתובת האימייל של חשבון Google

המאמרים הבאים
-------------

*   [שיטות מומלצות לשימוש בחשבונות שירות](https://docs.cloud.google.com/iam/docs/best-practices-service-accounts?hl=he) ו[מפתחות לחשבונות שירות](https://docs.cloud.google.com/iam/docs/best-practices-for-managing-service-account-keys?hl=he)
*   [חיפוש של פרטי הכניסה באמצעות ADC](https://docs.cloud.google.com/docs/authentication/application-default-credentials?hl=he)
*   [אימות לצורך שימוש בספריות לקוח ב-Cloud](https://docs.cloud.google.com/docs/authentication/client-libraries?hl=he)
*   [מידע על שיטות אימות](https://docs.cloud.google.com/docs/authentication?hl=he)

אלא אם צוין אחרת, התוכן של דף זה הוא ברישיון [Creative Commons Attribution 4.0](https://creativecommons.org/licenses/by/4.0/) ודוגמאות הקוד הן ברישיון [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0). לפרטים, ניתן לעיין ב[מדיניות האתר Google Developers‏](https://developers.google.com/site-policies?hl=he).‏ Java הוא סימן מסחרי רשום של חברת Oracle ו/או של השותפים העצמאיים שלה.

עדכון אחרון: 2026-11-11 (שעון UTC).
