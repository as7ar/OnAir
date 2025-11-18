# 📺 OnAir

## Change Log
[CHANGELOG](./ChangeLog.md)

## 📃 Skript
```scss
on streaming chat:
    broadcast "채팅: %platform of chat% - %chat user's id%: %content of the chat%"

on streaming donate:
    broadcast "후원: %platform of donate% - %donate user's id%: %content of donate message% (%donate amount%원)"

on streaming connection:
    broadcast "%player% 님이 %platform of connection% 계정을 연동했습니다!"

on streaming disconnection:
    broadcast "%player% 님이 %platform of disconnection% 계정 연동을 해제했습니다!"
```

## ⛓️ PlaceholderAPI
### 기본 정보
- `%onair_connection%` - 연동된 플랫폼 목록
- `%onair_check_connection_<id>%` - 특정 연동 상태 확인
  - `<id>`: CHZZK, YOUTUBE, TOONATION, TWITCH, SOOP
- `%onair_<id>_(display|id)%` - 플레이어 연동 채널 확인