with open(r'C:\Users\arnau\Desktop\JojoTV\streamvault-data\src\main\java\com\streamvault\data\remote\stalker\OkHttpStalkerApiService.kt', encoding='utf-8') as f:
    content = f.read()

old = '''            val body = response.body ?: throw IOException("Portal returned an empty response${actionSuffix(action)}.")
            val charset = body.contentType()?.charset(Charsets.UTF_8) ?: Charsets.UTF_8
            val reader = JsonReader(InputStreamReader(body.byteStream(), charset))
            reader.isLenient = true
            try {
                streamStalkerItems(reader, onItem)
            } catch (error: IllegalStateException) {
                throw IOException("Portal returned unreadable JSON${actionSuffix(action)}.", error)
            }'''

new = '''            val body = response.body ?: throw IOException("Portal returned an empty response${actionSuffix(action)}.")
            val charset = body.contentType()?.charset(Charsets.UTF_8) ?: Charsets.UTF_8
            val rawBytes = body.bytes()
            val preview = rawBytes.take(500).toByteArray().toString(charset)
            Log.d("StalkerRawDebug", "get_all_channels raw[0..500]: $preview")
            val reader = JsonReader(InputStreamReader(rawBytes.inputStream(), charset))
            reader.isLenient = true
            try {
                streamStalkerItems(reader, onItem)
            } catch (error: IllegalStateException) {
                throw IOException("Portal returned unreadable JSON${actionSuffix(action)}.", error)
            }'''

if old in content:
    content = content.replace(old, new)
    with open(r'C:\Users\arnau\Desktop\JojoTV\streamvault-data\src\main\java\com\streamvault\data\remote\stalker\OkHttpStalkerApiService.kt', 'w', encoding='utf-8') as f:
        f.write(content)
    print('Done')
else:
    print('Pattern not found')
