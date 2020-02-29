[![Build Status](https://app.bitrise.io/app/2cbe9beac1c1608b/status.svg?token=XC2UU4SVXH0riraHDOROvQ&branch=master)](https://app.bitrise.io/app/2cbe9beac1c1608b)
![](https://github.com/YusukeSuzuki1213/fcm-android/workflows/Run%20inspection%20on%20push/badge.svg)

# fcm-android
Firebase Cloud Messaging(=FCM)など通知に関連するコード、CI等を実装。
- [x] FCM通知
- [x] Locla Push通知
- [x] Slack WebhookへFCMペイロードを送信
- [x] ktlintの設定
- [x] BitriseでCIをまわす
- [x] Github ActionsとDangerを連携しLintチェックをする
- [ ] DeployGate
- [ ] Firebase Test Lab
- [ ] 通知チャンネルを使用し通知の種類を増やす
- [ ] commit時にlintが通らなかったらPushできないように
- [x] APIのキーの管理をスマートに
