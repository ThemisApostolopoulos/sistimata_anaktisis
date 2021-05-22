import numpy.linalg
from numpy.linalg import norm

queries = numpy.loadtxt('queryResults.txt', skiprows=0, unpack= False)
queries = queries.transpose()


for rank in range(3):
    if rank == 0:
        Ak50 = numpy.loadtxt('rankResults50.txt', skiprows=0, unpack=False)
        results50 = Ak50.dot(queries) / (norm(queries) * norm(Ak50))
        results = results50.transpose()
        name = "rank50"
    elif rank == 1:
        Ak150 = numpy.loadtxt('rankResults150.txt', skiprows=0, unpack=False)
        results150 = Ak150.dot(queries) / (norm(queries) * norm(Ak150))
        results = results150.transpose()
        name = "rank150"
    else:
        Ak300 = numpy.loadtxt('rankResults300.txt', skiprows=0, unpack=False)
        results300 = Ak300.dot(queries) / (norm(queries) * norm(Ak300))
        results = results300.transpose()
        name = "rank300"


    rows = len(results)
    columns = len(results[0])
    print(rows)
    print(columns)

    for numberOfDocs in range(3):
        if numberOfDocs == 0:
            k = 20
            file = name + "CosResults20.txt"
        elif numberOfDocs == 1:
            k = 30
            file = name + "CosResults30.txt"
        elif numberOfDocs == 2:
            k = 50
            file = name + "CosResults50.txt"


        a_file = open(file, "w")
        for q in range(rows):
            print(q)
            ind = (-results[q]).argsort()[:k]
            for x in ind:
                a_file.write(str(q + 1) + "    0    " + str(int(x) + 1) + "    0    " + str(results[q][x]) + "    STANDARD")
                a_file.write("\n")
        a_file.close()
