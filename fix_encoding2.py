import os

root = r'C:\Users\arnau\Desktop\JojoTV\app\src\main\java\com\nuvio\tv'

total = 0
for dirpath, _, files in os.walk(root):
    for fname in files:
        if not fname.endswith('.kt'):
            continue
        path = os.path.join(dirpath, fname)
        # Essayer UTF-8 d'abord
        try:
            with open(path, encoding='utf-8') as f:
                content = f.read()
        except UnicodeDecodeError:
            # Lire en latin-1, reecrire en UTF-8
            with open(path, encoding='latin-1') as f:
                content = f.read()
            content = content.lstrip('\ufeff')
            with open(path, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f'Converted latin-1->utf-8: {fname}')
            total += 1
            continue

        # Supprimer BOM + corriger sequences corrompues
        fixes = {
            'Ã©': '\u00e9', 'Ã¨': '\u00e8', 'Ãª': '\u00ea', 'Ã«': '\u00eb',
            'Ã ': '\u00e0', 'Ã¢': '\u00e2', 'Ã¹': '\u00f9', 'Ã»': '\u00fb',
            'Ã®': '\u00ee', 'Ã¯': '\u00ef', 'Ã´': '\u00f4', 'Å\x93': '\u0153',
            'Ã§': '\u00e7', 'Ã‰': '\u00c9', 'Ã€': '\u00c0', 'Ã‡': '\u00c7',
            'Ã\x89': '\u00c9', 'Ã\x80': '\u00c0',
        }
        original = content
        content = content.lstrip('\ufeff')
        for bad, good in fixes.items():
            content = content.replace(bad, good)
        if content != original:
            with open(path, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f'Fixed utf-8: {fname}')
            total += 1

print(f'\nTotal: {total} fichiers corriges')
