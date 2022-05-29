from PIL import Image
from mcrcon import MCRcon
import time

im = Image.open('pacman2.png')
pix = im.load()
size = im.size

listAmount = 0

sX = 0
sY = -60
sZ = 0

mcr = MCRcon("localhost", "minecraft")
mcr.connect()

#Y: 28 (start: 8)

print(size)
compare = pix[8,8]


for y in range(0, 28):
    list = []
    for x in range(0, 28):
        pixel = pix[x * 28 + 8, y * 28 + 8]
        if pixel == compare:
            list.append("t")
        else:
            list.append("f")

    print(list)
    i = 0
    while i < len(list):
        element = list[i]
        mcr.command("fill {} -60 {} {} -60 {} minecraft:black_wool".format(
            0 - (listAmount * 6),
            sZ + (i * 5),
            0 - ((listAmount + 1) * 6),
            (sZ + (i * 5)) + 4))
        if element == 't':
            mcr.command("fill {} -60 {} {} -55 {} minecraft:blue_wool".format(
                0 - (listAmount*6),
                sZ + (i*5),
                0 - ((listAmount+1) * 6),
                (sZ + (i*5)) + 4))
        if element == 'f':
            mcr.command("setblock {} -59 {} minecraft:stone_button[face=floor,facing=west]".format(
                0 - (listAmount * 6)-3,
                sZ + (i * 5) + 2
            ))
        i += 1
    listAmount = listAmount+1

mcr.disconnect()
