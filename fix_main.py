with open(r'C:\Users\arnau\Desktop\JojoTV\app\src\main\java\com\nuvio\tv\MainActivity.kt', encoding='utf-8') as f:
    lines = f.readlines()

launcher_block = (
    '    private val recordingViewModel: IptvRecordingViewModel by viewModels()\n'
    '\n'
    '    private val folderPickerLauncher = registerForActivityResult(\n'
    '        ActivityResultContracts.OpenDocumentTree()\n'
    '    ) { uri ->\n'
    '        if (uri != null) {\n'
    '            contentResolver.takePersistableUriPermission(\n'
    '                uri,\n'
    '                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION or\n'
    '                    android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION\n'
    '            )\n'
    '            val displayName = uri.lastPathSegment\n'
    '                ?.substringAfterLast(":")\n'
    '                ?.substringAfterLast("/")\n'
    '                ?: uri.toString()\n'
    '            recordingViewModel.updateStorageFolder(uri.toString(), displayName)\n'
    '        }\n'
    '    }\n'
    '\n'
)

target = '    private var isFirstResumeAfterCreate = false'
idx = next((i for i, l in enumerate(lines) if target in l), None)
if idx is not None and 'folderPickerLauncher' not in ''.join(lines):
    lines.insert(idx, launcher_block)
    print('Launcher inserted at', idx)
else:
    print('Launcher already present or target not found, idx=', idx)

content = ''.join(lines)
lines2 = content.splitlines(keepends=True)
occurrences = [i for i, l in enumerate(lines2) if 'LocalContentFocusRequester provides contentFocusRequester' in l]
print('Occurrences:', occurrences)

if len(occurrences) >= 2 and 'LocalPickFolderLauncher provides' not in content:
    idx2 = occurrences[1]
    lines2[idx2] = lines2[idx2].rstrip('\n').rstrip(',') + ',\n'
    lines2.insert(idx2 + 1, '                    LocalPickFolderLauncher provides { folderPickerLauncher.launch(null) }\n')
    print('Provider inserted after line', idx2)

with open(r'C:\Users\arnau\Desktop\JojoTV\app\src\main\java\com\nuvio\tv\MainActivity.kt', 'w', encoding='utf-8') as f:
    f.writelines(lines2)
print('Done')
