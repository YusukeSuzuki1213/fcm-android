[![Build Status](https://app.bitrise.io/app/2cbe9beac1c1608b/status.svg?token=XC2UU4SVXH0riraHDOROvQ&branch=master)](https://app.bitrise.io/app/2cbe9beac1c1608b)
![](https://github.com/YusukeSuzuki1213/fcm-android/workflows/Inspection/badge.svg)
![](https://github.com/YusukeSuzuki1213/fcm-android/workflows/DeployGate/badge.svg)

# fcm-android
Firebase Cloud Messaging(=FCM)など通知に関連するコード、CI/CD等を実装。

# 内容
- [x] FCM通知
- [x] Local Push通知
    - [x] FCM通知をハンドリングし、Local Pushとして通知
- [x] Slack WebhookへFCMのデバイストークンを送信
    - [x] Retrofit2とコールバック
    - [x] Retrofit2とCoroutines
    - [x] Fuelとコールバック
    - [x] FuelとCoroutines
- [x] ktlintの設定
- [x] BitriseでCIをまわす
- [x] Github ActionsとDangerを連携
    - [x] PR時にGithub ActionsでLintチェック
    - [x] Lintチェックの指摘箇所をPRコメントとして表示
- [x] gitのpre-commit hookを使用しcommit時にLintチェックをする
- [x] APIのキーの管理をスマートに
- [x] DeployGateでアプリ配布を自動化
    - [x] masterに`push`されたら、DeployGate上のAPKを更新
- [ ] Firebase Test Lab
- [ ] 通知チャンネルを使用し通知の種類を増やす
- [ ] Daggerで依存性を注入
- [ ] アニメーション

# 配布
アプリは以下のURLからDLできます。(DLにはパスワードが必要です)
[](https://dply.me/2gsf3x)
