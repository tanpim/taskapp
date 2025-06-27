# 自己管理デスクトップアプリ - Todo Calendar

## 📋 概要

月・週・日単位でタスクを整理・可視化する自己管理デスクトップアプリです。
Googleカレンダーとの連携とDiscord経由でのモバイル確認機能を備えています。

## 🎯 主な機能

- **月間目標管理**: 月単位の目標を設定・管理
- **週間ToDoリスト**: チェックボックス付きの週間タスク管理
- **週間スケジュール表示**: 7:00〜22:00の時間軸で予定を可視化
- **今日のToDo**: 日次タスクの追加・管理
- **Googleカレンダー連携**: OAuth2.0認証で予定を取得
- **Discord連携**: スクリーンショットをDiscordに自動投稿

## 🛠️ 技術スタック

- **GUI**: JavaFX 17
- **ビルドツール**: Maven
- **データ保存**: JSON
- **API連携**: Google Calendar API
- **通知**: Discord Bot (JDA)

## 🚀 セットアップ

### 前提条件

- Java 17以上
- Maven 3.6以上

### インストール手順

1. リポジトリをクローン
```bash
git clone <repository-url>
cd todocalendarapp
```

2. 依存関係をインストール
```bash
mvn clean install
```

3. アプリケーションを実行
```bash
mvn javafx:run
```

## 📁 プロジェクト構造

```
todocalendarapp/
├── src/
│   ├── main/
│   │   ├── java/com/todocalendar/
│   │   │   ├── Main.java                 # アプリケーションエントリーポイント
│   │   │   └── controller/
│   │   │       └── MainController.java   # UIコントローラー
│   │   └── resources/
│   │       └── fxml/
│   │           └── MainView.fxml         # UIレイアウト
├── docs/
│   └── requirements_v1.1.md              # 要件定義書
├── pom.xml                               # Maven設定
└── README.md                             # このファイル
```

## 🎨 画面構成

```
┌────────────────────────────────────────────┐
│ 月間目標（TextArea） ← 月1で見直す場所                │
├──────────────┬────────────────────────────┤
│ 今週のToDoリスト     │ 今週の週間予定グリッド（Googleカレンダー連携） │
│ チェックボックス付き │ 7:00〜22:00 × 月〜日マスの表形式で表示       │
├──────────────┴────────────────────────────┤
│ 今日のToDo（追加欄）← 翌日の準備にも活用できる          │
└────────────────────────────────────────────┘
```

## 📝 開発ステップ

### Step 1: ✅ JavaFX UIの基本レイアウト作成
- [x] プロジェクト構造の作成
- [x] Maven設定ファイル
- [x] 基本UIレイアウト（FXML）
- [x] コントローラークラス

### Step 2: 🔄 ToDoのチェック保存と読込の実装
- [ ] JSON形式でのデータ保存
- [ ] データの読み込み機能
- [ ] チェックボックスの状態管理

### Step 3: 🔄 Google Calendar連携
- [ ] OAuth2.0認証の実装
- [ ] カレンダーイベントの取得
- [ ] 週間スケジュールへの表示

### Step 4: 🔄 スクリーンショット保存機能
- [ ] JavaFXノードの画像化
- [ ] 画像ファイルの保存

### Step 5: 🔄 Discord Bot実装
- [ ] Discord Botの作成
- [ ] 画像投稿機能

### Step 6: 🔄 更新検知タイミング
- [ ] 自動保存機能
- [ ] 手動更新機能

## 🔧 設定

### Google Calendar API設定
1. Google Cloud Consoleでプロジェクトを作成
2. Google Calendar APIを有効化
3. OAuth2.0認証情報を作成
4. `credentials.json`をプロジェクトに配置

### Discord Bot設定
1. Discord Developer Portalでアプリケーションを作成
2. Botトークンを取得
3. 設定ファイルにトークンを記載

## 📄 ライセンス

このプロジェクトは個人使用を目的としています。

## 🤝 貢献

現在は個人プロジェクトですが、改善提案は歓迎します。 