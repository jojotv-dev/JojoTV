with open(r'C:\Users\arnau\Desktop\JojoTV\app\src\main\java\com\nuvio\tv\MainActivity.kt', encoding='utf-8') as f:
    lines = f.readlines()

# 1. Ajouter LocalPickFolderLauncher dans le vrai CompositionLocalProvider (ligne 499)
target = '                    com.nuvio.tv.core.player.LocalTrailerPlayerPool provides trailerPlayerPool\n'
for i, l in enumerate(lines):
    if l == target:
        lines[i] = '                    com.nuvio.tv.core.player.LocalTrailerPlayerPool provides trailerPlayerPool,\n'
        lines.insert(i + 1, '                    LocalPickFolderLauncher provides { folderPickerLauncher.launch(null) }\n')
        print('Provider added at line', i + 2)
        break

# 2. Retirer le LocalPickFolderLauncher mal place dans ModernSidebarScaffold
new_lines = []
skip_next = False
for i, l in enumerate(lines):
    if skip_next:
        skip_next = False
        continue
    # Supprimer la virgule ajoutee sur LocalContentFocusRequester provides contentFocusRequester
    # et la ligne LocalPickFolderLauncher provides qui suit, dans ModernSidebarScaffold
    if 'LocalContentFocusRequester provides contentFocusRequester,' in l:
        # Verifier que la ligne suivante est le LocalPickFolderLauncher mal place
        if i + 1 < len(lines) and 'LocalPickFolderLauncher provides' in lines[i + 1]:
            new_lines.append(l.replace('contentFocusRequester,', 'contentFocusRequester'))
            skip_next = True
            print('Removed bad provider at line', i + 2)
            continue
    new_lines.append(l)

with open(r'C:\Users\arnau\Desktop\JojoTV\app\src\main\java\com\nuvio\tv\MainActivity.kt', 'w', encoding='utf-8') as f:
    f.writelines(new_lines)
print('Done')
