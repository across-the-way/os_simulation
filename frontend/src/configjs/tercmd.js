export const tercmd = [
    {
        "key": "pwd",
        "title": "Pwd",
        "group": "filesystem",
        "usage": "pwd",
        "description": "Show current directory",
        "example": [
          {
            "des": "show the path.",
            "cmd": "pwd"
          },
        ]
      },
      {
        "key": "cd",
        "title": "Cd",
        "group": "filesystem",
        "usage": "cd [folder]",
        "description": "open a folder",
        "example": [
          {
            "des": "get into the selected directory",
            "cmd": "cd folder"
          },
        ]
      },
      {
        "key": "touch",
        "title": "Touch",
        "group": "filesystem",
        "usage": "touch [filename]",
        "description": "Create a new file.",
        "example": [
          {
            "des": "Create the file 'file.txt'",
            "cmd": "touch file.txt"
          },
        ]
      },
      {
        "key": "mkdir",
        "title": "Mkdir",
        "group": "filesystem",
        "usage": "mkdir [dir]",
        "description": "Create a folder.",
        "example": [
          {
            "des": "Create the directory folder",
            "cmd": "mkdir folder"
          },
        ]
      },
      {
        "key": "cat",
        "title": "Cat",
        "group": "filesystem",
        "usage": "cat [file]",
        "description": "see the content in file.",
        "example": [
          {
            "des": "see what in file.txt.",
            "cmd": "cat file.txt"
          },
        ]
      },
      {
        "key": "rm",
        "title": "Help",
        "group": "filesystem",
        "usage": "rm [pattern]",
        "description": "Delete sth.",
        "example": [
          {
            "des": "Delete the file.",
            "cmd": "rm file.txt"
          },
          {
            "des": "Delete the folder.",
            "cmd": "rm -r folder"
          },
        ]
      },
      {
        "key": "ls",
        "title": "Ls",
        "group": "filesystem",
        "usage": "ls",
        "description": "Show the content under current path.",
        "example": [
          {
            "des": "Show the content under /filesystem.",
            "cmd": "/filesystem > ls"
          },
        ]
      },
      {
        "key": "help",
        "title": "Help",
        "group": "local",
        "usage": "help [pattern]",
        "description": "Show command document.",
        "example": [
          {
            "des": "Get help documentation for exact match commands.",
            "cmd": "help open"
          },
          {
            "des": "Get help documentation for fuzzy matching commands.",
            "cmd": "help *e*"
          },
          {
            "des": "Get help documentation for specified group, match key must start with ':'.",
            "cmd": "help :groupA"
          }
        ]
      },
      {
        "key": "clear",
        "title": "Clear logs",
        "group": "local",
        "usage": "clear [history]",
        "description": "Clear screen or history.",
        "example": [
          {
            "cmd": "clear",
            "des": "Clear all records on the current screen."
          },
          {
            "cmd": "clear history",
            "des": "Clear command history."
          }
        ]
      },
      {
        "key": "open",
        "title": "Open page",
        "group": "local",
        "usage": "open <url>",
        "description": "Open a specified page.",
        "example": [
          {
            "cmd": "open blog.beifengtz.com"
          }
        ]
      }
]