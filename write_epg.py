import os
path = r'C:\Users\arnau\Desktop\JojoTV\app\src\main\java\com\nuvio\tv\ui\screens\iptv\IptvEpgScreen.kt'
with open(path, 'w', encoding='utf-8') as f:
    f.write(open(r'C:\Users\arnau\Desktop\JojoTV\epg_screen.txt', encoding='utf-8').read())
print('OK')
