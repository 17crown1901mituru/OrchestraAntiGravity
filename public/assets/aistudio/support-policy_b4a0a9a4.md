# Google Cloud 的 Buildpack 支持政策 &nbsp;|&nbsp; Buildpacks &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/buildpacks/support-policy

Google Cloud 的 Buildpack 使用由各自社区维护的开源组件。Google Cloud 的 Buildpack 会发布包含构建和运行应用或函数所需组件的[构建器](https://docs.cloud.google.com/docs/buildpacks/builders?hl=zh-cn)。每个构建器都与特定的操作系统 (OS) 发行版相关联，并且具有自己的一组受支持的组件和语言运行时。

在[正式版 (GA)](https://cloud.google.com/products?hl=zh-cn#product-launch-stages) 期间，Google 为构建器提供支持。 新构建器通常会遵循 Ubuntu LTS 版本，并会在 Google Cloud 的 Buildpack [版本说明](https://docs.cloud.google.com/docs/buildpacks/release-notes?hl=zh-cn)中公布。

在正式版支持期内：

*   构建器组件（操作系统软件包、库、语言运行时等）会定期使用安全修复和 bug 修复进行更新。
*   为保持稳定性，Google Cloud 的 Buildpack 会避免在构建器中实现重大功能或更改。破坏性更改会提前在 Google Cloud 的 Buildpack [版本说明](https://docs.cloud.google.com/docs/buildpacks/release-notes?hl=zh-cn)中公布。

如果系统不再主动维护构建器的操作系统发行版（例如停止支持 Ubuntu），则 Google Cloud 的 Buildpack 构建器可能会被弃用，并且最终会停用。

这涉及三个方面：弃用日期发布、弃用期和停用日期。[支持时间表](#support-schedule)部分中发布的日期指明弃用期的开始日期以及停用日期。

在弃用期内，您通常可以继续使用该构建器创建新应用并更新现有应用。您应利用这段时间将使用已弃用的构建器的应用或函数迁移到最新的构建器。

构建器映像将继续在注册表中可用，但包含一个额外的 `<builder-version>-sunset` 标记，例如 `v1-sunset`。使用弃用构建器构建的容器映像将继续存在于注册表中，可在存储位置部署，并继续正常运行。

Google 可能会根据 Google Cloud 服务使用协议条款，来更改任何构建器的支持时间表或生命周期

支持时间表
-----

以下列表包含支持的 Google Cloud Buildpack 构建器，以及已弃用和停用的构建器。

| 标记 | 位置 | 操作系统 | 弃用 | 弃用 |
| --- | --- | --- | --- | --- |
| `google-24` | `[gcr.io/buildpacks/builder:google-24](https://gcr.io/buildpacks/builder:google-24)` | Ubuntu 24 | 2029 年 4 月 | 2030 年 4 月 |
| `google-22` | `[gcr.io/buildpacks/builder:google-22](https://gcr.io/buildpacks/builder:google-22)`、`latest` | Ubuntu 22 | 2027 年 4 月 | 2028 年 4 月 |
| `v1` | `[gcr.io/buildpacks/builder:v1](https://gcr.io/buildpacks/builder:v1)` | Ubuntu 18 | 2024 年 12 月 | 2025 年 4 月 |

`latest` 标记始终指向 `google-22` 标记。

重要日期可能会因关键组件的稳定性和可用性而延迟。请定期查看此页面以了解最新信息。

语言和构建器
------

Google Cloud 的 Buildpack 提供的每个构建器支持不同的操作系统软件包和语言运行时版本。如需了解详情，请参阅[每个构建器支持的语言](https://docs.cloud.google.com/docs/buildpacks/builders?hl=zh-cn)列表。

语言运行时有其自己的支持和维护时间表，由各自的社区提供。需要注意以下两个方面：

*   支持的构建器提供的语言版本可能不受其社区的支持。我们建议您尽快更新到较新且获得主动支持的语言版本。
*   构建器的底层操作系统可能不再受支持，但您的首选语言版本仍得到主动维护。在这种情况下，我们建议您使用一个主动维护的操作系统社区更新到最新的构建器版本。
