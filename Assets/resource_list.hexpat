#include "hex/core.pat"

#pragma endian big

using int = s32;
using ushort = u16;
using long = s64;


using Entry;
fn entry_formatter(Entry entry) {
    return entry.path;
};

struct Entry {
    ushort pathLength       [[color("1c3048")]];
    char   path[pathLength] [[color("3dad89")]];
    long   size             [[color("0f7af9")]];
} [[format("entry_formatter")]];


struct ResourceList {
    int   entryCount          [[color("ae4020")]];
    Entry entries[entryCount] [[inline]];
};

ResourceList list @ 0x00;

str projectRootPath in;

fn main() {
    for (int i = 0, i < list.entryCount, i += 1) {
        Entry entry @ addressof(list.entries[i]);
        hex::core::add_virtual_file("/" + entry.path, null);
    }
};
