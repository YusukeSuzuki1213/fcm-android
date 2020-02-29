[![Build Status](https://app.bitrise.io/app/2cbe9beac1c1608b/status.svg?token=XC2UU4SVXH0riraHDOROvQ&branch=master)](https://app.bitrise.io/app/2cbe9beac1c1608b)
![](https://github.com/YusukeSuzuki1213/fcm-android/workflows/Run%20inspection%20on%20push/badge.svg)

# fcm-android
Firebase Cloud Messaging(=FCM)など通知に関連するコード、CI等を実装。
- [x] FCM通知
- [x] Local Push通知
    - [x] FCM通知をハンドリングし、Local Pushとして通知
- [x] Slack WebhookへFCMペイロードを送信
    - [x] Retrofit2とコールバック
    - [x] Retrofit2とCoroutines
    - [x] Fuelとコールバック
    - [x] FuelとCoroutines
- [x] ktlintの設定
- [x] BitriseでCIをまわす
- [x] Github ActionsとDangerを連携
    - PR時にGithub ActionsでLintチェック
    - Lintチェックの指摘箇所をPRコメントとして表示
- [ ] DeployGate
- [ ] Firebase Test Lab
- [ ] 通知チャンネルを使用し通知の種類を増やす
- [x] gitのpre-commit hookを使用しcommit時にLintチェックをする
- [x] APIのキーの管理をスマートに

