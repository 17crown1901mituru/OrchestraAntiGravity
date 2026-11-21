# האצה של גרסאות build באמצעות קובצי אימג' ששמורים במטמון &nbsp;|&nbsp; Buildpacks &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/buildpacks/cache-images

תוכלו להשתמש ב[קובצי אימג' ששמורים במטמון](https://buildpacks.io/docs/app-developer-guide/using-cache-image/) של buildpacks כדי לעשות שימוש חוזר בתוצאות מ-build קודם. באופן כזה תוכלו לקצר את משך הזמן הכולל ליצירת קובצי אימג' בעזרת buildpacks של Google Cloud, גם כשבונים קובצי אימג' בסביבות זמניות כמו Cloud Build.

השלבים המפורטים במדריך הזה מתמקדים בפיתוח גרסאות build מרחוק באמצעות Cloud Build. לפיתוח של גרסאות build מקומיות, תוכלו לקרוא את ההוראות של [קובצי אימג' ששמורים במטמון](https://buildpacks.io/docs/app-developer-guide/using-cache-image/) ואת [קובץ העזר של פקודת `pack`](https://buildpacks.io/docs/tools/pack/cli/pack_build/).

שימוש בקובצי אימג' ששמורים במטמון עם Cloud Build
------------------------------------------------

כדי להפעיל קובצי אימג' ששמורים במטמון של buildpacks ב-Cloud Build אתם צריכים ליצור [קובץ תצורת build](https://docs.cloud.google.com/build/docs/configuring-builds/create-basic-configuration?hl=he), למשל `cloudbuild.yaml`. קובץ תצורת build מורה לפקודה `pack` ב-CLI לפרסם קובץ אימג' של build למאגר מרוחק שציינתם. קובץ האימג' של ה-build הזה ישמש לאחר מכן כ'קובץ אימג' ששמור במטמון' לגרסאות build נוספות.

שימו לב שה-build הראשון שאתם מפעילים עם קובץ תצורת build חדש משתמש בפקודה `pack` כדי ליצור, ולאחר מכן להעביר את המטמון של קובץ האימג' של ה-build הראשוני הזה למאגר שלכם. תוכלו להבחין בשיפור בביצועים של ה-build רק אחרי שקובץ האימג' הראשוני ששמור במטמון יהיה זמין לפעולות ה-build שלכם.

יצירת קובץ תצורת build
----------------------

כדי להגדיר קובץ תצורת build ב-Cloud Build, עליכם לכתוב קובץ YAML שכולל שלב build שמשתמש בפקודה `pack` ב-CLI ליצירת קובץ האימג'.

בקובץ לדוגמה `cloudbuild.yaml`, ה-build יוצר קובץ אימג' לאפליקציה או לפונקציה באמצעות buildpack ולאחר מכן יוצר את קובץ האימג' הראשוני ששמור במטמון ומעביר אותו למאגר:

options:
  logging: CLOUD\_LOGGING\_ONLY
  pool: {}
projectId: PROJECT\_ID
steps:
\- name: gcr.io/k8s-skaffold/pack
  entrypoint: pack
  args:
  \- build
  \- LOCATION\-docker.pkg.dev/PROJECT\_ID/REPO\_NAME/IMAGE\_NAME
  \- \--builder
  \- gcr.io/buildpacks/builder:latest
  \- \--cache-image
  \- LOCATION\-docker.pkg.dev/PROJECT\_ID/REPO\_NAME/CACHE\_IMAGE\_NAME:latest
  \- \--publish 

מחליפים את:

*   `LOCATION` בשם האזור של מאגר הקונטיינרים. לדוגמה: `us-west2`
*   ‫`PROJECT_ID` במזהה הפרויקט. Google Cloud
*   `REPO_NAME` בשם המאגר ב-Docker.
*   `IMAGE_NAME` בשם של קובץ האימג' בקונטיינר של האפליקציה או הפונקציה.
*   `CACHE_IMAGE_NAME` בשם של קובץ האימג' של ה-build ששמור במטמון.

מידע נוסף על ארגומנטים נוספים שאפשר להגדיר בקובץ תצורת build זמין ב[קובץ העזר של פקודת `pack build`](https://buildpacks.io/docs/tools/pack/cli/pack_build/).

פיתוח גרסאות build מרחוק עם קובצי אימג' ששמורים במטמון
------------------------------------------------------

כדי להריץ build, עליכם לכלול בפקודה את הדגל `--config` ולציין את קובץ התצורה שלכם. לדוגמה, כדי להשתמש בקובץ בשם `cloudbuild.yaml` אתם יכולים להריץ את הפקודה הבאה:

gcloud builds submit --config cloudbuild.yaml --pack image=example-docker.pkg.dev/my-project-id/repo/example-image

מידע נוסף על פיתוח גרסאות build מרחוק זמין במאמרים [פיתוח אפליקציה](https://docs.cloud.google.com/docs/buildpacks/build-application?hl=he#remote_builds) ו[פיתוח פונקציה](https://docs.cloud.google.com/docs/buildpacks/build-function?hl=he#remote_builds).
