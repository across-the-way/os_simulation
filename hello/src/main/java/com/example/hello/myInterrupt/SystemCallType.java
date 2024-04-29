package com.example.hello.myInterrupt;

public enum SystemCallType {
    ProcessNew,
    ProcessExit,
    Fork,

    FileNew,
    FileDelete,
    FileRead,
    FileWrite,
    DirNew,
    DirDelete,
    FileOpen,
    FileClose,
    FileFinish,

    IORequest,
}
