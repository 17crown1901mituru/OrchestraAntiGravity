# Gen AI &nbsp;|&nbsp; AI and ML &nbsp;|&nbsp; Google Cloud Documentation
URL: https://docs.cloud.google.com/docs/ai-ml/generative-ai/choose-models-infra-for-ai

Choose models and infrastructure for your generative AI application   
----------------------------------------------------------------------

Learn which products, frameworks, and tools are the best match for building your generative AI application. The following figure shows common components in a Cloud-hosted generative AI application.

1.  **Application hosting:** Compute to host your application. Your application can use [Google Cloud's client libraries and SDKs](https://docs.cloud.google.com/apis/docs/cloud-client-libraries) to talk to different Cloud products.
    
2.  **Model hosting:** Scalable and secure hosting for a generative model.
    
3.  **Model:** Generative model for text, chat, images, code, embeddings, and [multimodal](https://docs.cloud.google.com/vertex-ai/generative-ai/docs/learn/models).
    
4.  **[Grounding solution](https://docs.cloud.google.com/vertex-ai/docs/generative-ai/grounding/overview):** Anchor model output to verifiable, updated sources of information.
    
5.  **Database:** Store your application's data. You might reuse your existing database as your grounding solution, by augmenting prompts via SQL query, and/or storing your data as vector embeddings using an extension like [pgvector](https://cloud.google.com/blog/products/databases/announcing-vector-support-in-postgresql-services-to-power-ai-enabled-applications).
    
6.  **Storage:** Store files such as images, videos, or [static web frontends](https://docs.cloud.google.com/storage/docs/hosting-static-website). You might also use Storage for the raw grounding data (eg. PDFs) that you later convert into embeddings and store in a vector database.
    

The sections below walk through each of those components, helping you choose which Google Cloud products to try.

### Application hosting infrastructure

Choose a product to host and serve your application workload, which makes calls out to the generative model.

Want managed serverless infrastructure?

[Cloud Run](https://docs.cloud.google.com/run/docs/overview/what-is-cloud-run)

### Model hosting infrastructure

Google Cloud provides multiple ways to host a generative model, from the flagship Vertex AI platform, to customizable and portable hosting on Google Kubernetes Engine.

Using Gemini and need enterprise features like scaling, security, data privacy, and observability

[Gemini Developer API](https://ai.google.dev/docs)

Want fully managed infrastructure, with first-class generative AI tools and APIs?

[Vertex AI](https://docs.cloud.google.com/vertex-ai/docs/generative-ai/learn/overview)

Does your model require a specialized kernel, legacy OS, or have special licensing terms?

[Compute Engine](https://docs.cloud.google.com/compute/docs/gpus/about-gpus)

### Grounding and RAG

To ensure informed and accurate model responses, **[ground](https://docs.cloud.google.com/vertex-ai/docs/generative-ai/grounding/overview)** your generative AI application with [real-time data](https://cloud.google.com/blog/products/ai-machine-learning/how-to-use-grounding-for-your-llms-with-text-embeddings). This is called [retrieval-augmented generation (RAG)](https://cloud.google.com/use-cases/retrieval-augmented-generation).

If you want to generate content that’s grounded on up-to-date information from the internet, then Gemini models can evaluate whether the model's knowledge is sufficient or whether grounding with Google Search is required.

You can implement grounding using an index of your data with a search engine. Many search engines now store embeddings in a [vector database](https://docs.cloud.google.com/discover/what-is-a-vector-database), which is an optimal format for [operations like similarity search](https://cloud.google.com/blog/products/ai-machine-learning/how-to-use-grounding-for-your-llms-with-text-embeddings). Google Cloud offers multiple vector database solutions, for different use cases.

Note: You can ground using non-vector databases by querying an existing database like Cloud SQL or Firestore, and you can use the result of the query in your model prompt.

Do you want a fully-managed optimized solution that supports most data sources and prevents direct access to the underlying embeddings?

[Vertex AI Search](https://docs.cloud.google.com/generative-ai-app-builder/docs/try-enterprise-search)

close

You are building a search engine for RAG

Do you want to build a search engine for RAG using a managed orchestrator with a LlamaIndex-like interface?

[Vertex AI RAG Engine](https://docs.cloud.google.com/vertex-ai/generative-ai/docs/rag-overview)

close

You might use a [reference architecture](https://docs.cloud.google.com/architecture/ai-ml) to build a tailor-made search engine and a vector database for RAG use cases.

Do you need a low-latency vector search, large-scale serving, or a specialized and optimized vector database?

[Vertex AI Vector Search](https://docs.cloud.google.com/vertex-ai/docs/vector-search/overview)

Is your data accessed programmatically (OLTP)? Already using a SQL database?

Want to use Google AI models directly from your database? Require low latency?

Have a large analytical dataset (OLAP)? Require batch processing, and frequent SQL table access by humans or scripts (data science)?

[BigQuery](https://cloud.google.com/blog/products/ai-machine-learning/open-source-framework-for-connecting-llms-to-your-data)

Grounding with APIs
-------------------

Instead of (or in addition to) using your own data for grounding, many online services offer APIs that you can use to retrieve grounding data to augment your model prompt.

Start building
--------------
