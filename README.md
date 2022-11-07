# HacoCMS Android SDK

[hacoCMS](https://hacocms.com/)のAndroid用 API クライアントライブラリです。

## Step 1. hacoCMSのAPIスキーマ設定
お持ちのhacoCMSアカウントの適当なプロジェクト（無ければ作成してください）に、ブログ記事のAPIを以下の設定で作成してください。[APIの作成方法についてはhacoCMSのドキュメント](https://hacocms.com/docs/entry/api-create)をご確認ください。

- API名：`記事`（任意）
- エンドポイント：`entries`
- 説明文：（任意）
- APIの型：`リスト形式`
- APIスキーマ：`下記の表と画像を参照`

| # | フィールドタイプ              | フィールド名（任意） | フィールド ID     |
| --|----------------------| -----------------|-----------------|
| 1 | テキストフィールド       | タイトル          | `title`          |
| 2 | テキストフィールド       | 概要             | `description`    |
| 3 | リッチテキスト          | 本文             | `body`           |

記事APIを作成できたら、適当な記事をいくつか作成してみましょう。[コンテンツの作成方法についてはhacoCMSのドキュメント](https://hacocms.com/docs/entry/contents-create)をご確認ください。


## Step 2. hacoCMS SDKのインストール

```
implementation "com.hacocms.sdk:hacocms:1.0.0"
```

## Step 3. 使い方

クライアント対象の作成
```kotlin
    val client = HacoCmsClient.create(
        baseUrl = "BASE_URL",
        accessToken = "ACCESS_TOKEN",
        projectDraftToken = "PROJECT_DRAFT_TOKEN"
    )
```

APIから返ってきた結果をmapingするために、 ApiContentからextendsされたObjectを作ります。
```kotlin
data class ExampleContent(
    @SerializedName("id") override val id: String,
    @SerializedName("createdAt") override val createdAt: LocalDateTime,
    @SerializedName("updatedAt") override val updatedAt: LocalDateTime,
    @SerializedName("publishedAt") override val publishedAt: LocalDateTime?,
    @SerializedName("closedAt") override val closedAt: LocalDateTime?,
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("body") val body: String?,
) : ApiContent
```

APIから返ってきた結果を処理するためのコードです。
```kotlin
    // Gets list content.
    client.getList<ExampleContent>(endpoint = "/example")

    // Gets list content with custom query.
    client.getList<ExampleContent>(
        endpoint = "/example",
        query = QueryParameters(limit = LIMIT, offset = OFFSET)
    )

    // Gets content by content id.
    client.getContent<ExampleContent>(endpoint = "/example", id = "CONTENT_ID")

    // Gets single object.
    client.getSingle<ExampleContent>(endpoint = "/single")

    // Gets including draft.
    client.getListIncludingDraft<ExampleContent>(
        endpoint = "/example",
        query = QueryParameters(search = "abc", s = listOf("createdAt".sq().desc())),
    )
```

備考:  
Android Manifest内でインターネットアクセス権限を追加してください。
```kotlin
    <uses-permission android:name="android.permission.INTERNET" />
```
コード記述例 : https://github.com/hacocms/hacocms-jvm-sdk/tree/main/examples/android/app

[hacoCMS APIリファレンス](https://hacocms.com/references/content-api)

# LICENSE

MIT License
