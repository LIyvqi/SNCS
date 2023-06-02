author_list=list()
with open("D:\pythonProject\CSHDataAnalysics\caseStudy\community-53-14-70.txt", 'r', encoding="utf-8") as f:
    line = f.readline()
    authors = line.strip("\n").split(" ")[4:]
    print(authors)  
    with open("D:\pythonProject\CSHDataAnalysics\caseStudy\AMiner-Paper-Author-Index.txt",'r',encoding="utf-8") as f:
        lines=f.readlines()
        for oneLine in lines:
            index=oneLine.strip('\n').split(" ")[-1]
            author=oneLine.strip(" "+index+"\n")
            if index in authors:
                author_list.append(author)
 
print(author_list)
description=dict()

with open("D:\pythonProject\CSHDataAnalysics\caseStudy\AMiner-Author.txt",'r',encoding="utf-8") as f:
    lines=f.readlines()
    for i in range(0,len(lines)):
        line=lines[i]
        if line[0:2]=="#n":
            for author in author_list:
                if author in line:
                    for i in range(i+1,i+8):
                        line1=lines[i]
                        if line1[0:2]=="#t":
                            if author in description.keys():
                                description[author].append(line1[3:])
                            else:
                                description.update({author:list()})   # description的格式为{author:[topics]}
                                description[author].append(line1[3:])

keyWord_people_total_nums=dict()
distribution=dict()
for lines in description.values():
    keyWord_single_people_set = set()
    for line in lines:
        line=line.strip('\n').split(";")
        for keyWord in line:
            keyWord_single_people_set.add(keyWord)

    for keyWord in keyWord_single_people_set:
        if keyWord in distribution.keys():
            distribution[keyWord]=distribution.get(keyWord)+1
        else:
            distribution.update({keyWord:1})


with open("communityKeyWordDis.csv",'w',encoding="utf-8") as f:
    for keyWord in distribution.keys():
        if distribution[keyWord]>=10:
            f.write(keyWord+","+str(distribution.get(keyWord)))
            f.write("\n")




keyWordsofQuery=list()
for line in description.get("Michael R. Lyu"):
    line=line.strip("\n").split(";")
    for word in line:
        keyWordsofQuery.append(word)

keyWordsofQueryDis=dict()
for keyWord in keyWordsofQuery:
    keyWordsofQueryDis.update({keyWord:distribution.get(keyWord)})


print(sorted(keyWordsofQueryDis.items(), key=lambda item:item[1]))

with open("queryCommunityKeyWordDis.csv", 'w', encoding="utf-8") as f:
    for keyWord in keyWordsofQueryDis.keys():
        f.write(keyWord+","+str(keyWordsofQueryDis.get(keyWord)))
        f.write('\n')


author_list=list()
with open("D:\pythonProject\CSHDataAnalysics\caseStudy\mp_1331_community-53-14-0.txt", 'r', encoding="utf-8") as f:
    line = f.readline()
    authors = line.strip("\n").split(" ")[4:]
    print(authors)  
    with open("D:\pythonProject\CSHDataAnalysics\caseStudy\AMiner-Paper-Author-Index.txt",'r',encoding="utf-8") as f:
        lines=f.readlines()
        for oneLine in lines:
            index=oneLine.strip('\n').split(" ")[-1]
            author=oneLine.strip(" "+index+"\n")
            if index in authors:
                author_list.append(author)

print(author_list)
description=dict()

with open("D:\pythonProject\CSHDataAnalysics\caseStudy\AMiner-Author.txt",'r',encoding="utf-8") as f:
    lines=f.readlines()
    for i in range(0,len(lines)):
        line=lines[i]
        if line[0:2]=="#n":
            for author in author_list:
                if author in line:
                    for i in range(i+1,i+8):
                        line1=lines[i]
                        if line1[0:2]=="#t":
                            if author in description.keys():
                                description[author].append(line1[3:])
                            else:
                                description.update({author:list()})   # description  {author:[topics]}
                                description[author].append(line1[3:])

keyWord_people_total_nums=dict()
distribution=dict()
for lines in description.values():
    keyWord_single_people_set = set()
    for line in lines:
        line=line.strip('\n').split(";")
        for keyWord in line:
            keyWord_single_people_set.add(keyWord)

    for keyWord in keyWord_single_people_set:
        if keyWord in distribution.keys():
            distribution[keyWord]=distribution.get(keyWord)+1
        else:
            distribution.update({keyWord:1})


with open("MP_communityKeyWordDis.csv",'w',encoding="utf-8") as f:
    for keyWord in distribution.keys():
        if distribution[keyWord]>=10:
            f.write(keyWord+","+str(distribution.get(keyWord)))
            f.write("\n")


keyWordsofQuery=list()
for line in description.get("Michael R. Lyu"):
    line=line.strip("\n").split(";")
    for word in line:
        keyWordsofQuery.append(word)

keyWordsofQueryDis=dict()
for keyWord in keyWordsofQuery:
    keyWordsofQueryDis.update({keyWord:distribution.get(keyWord)})


print(sorted(keyWordsofQueryDis.items(), key=lambda item:item[1]))

with open("MP_queryCommunityKeyWordDis.csv", 'w', encoding="utf-8") as f:
    for keyWord in keyWordsofQueryDis.keys():
        f.write(keyWord+","+str(keyWordsofQueryDis.get(keyWord)))
        f.write('\n')

