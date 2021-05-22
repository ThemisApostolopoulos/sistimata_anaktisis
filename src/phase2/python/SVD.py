import re
import sys
import numpy
import numpy.linalg

A = numpy.loadtxt('results.txt', skiprows=0, unpack= False)
A = A.transpose()


numpy.set_printoptions(formatter={"float": lambda x:("%2.3f" %x)})

print("SVD:")
print()
U, S, V = numpy.linalg.svd(A)
print("U:")
print(U)
print()
print("S:")
print(S)
print()
print("V:")
print(V)
print()
S = numpy.diag(S)
for x in range(3):
    if x == 0:
        k = 50
        file = "rankResults50.txt"
    elif x == 1:
        k = 150
        file = "rankResults150.txt"
    elif x == 2:
        k = 300
        file = "rankResults300.txt"

    print("Rank %d approximation of A:" % k)
    Uk = U[:, :k]
    Sk = S[:k, :k]
    Vk = V[:k, :]
    Ak = Uk.dot(Sk).dot(Vk)
    print(Ak)

    rows = len(Ak)
    columns = len(Ak[0])
    a_file = open(file, "w")
    for j in range(columns):
        for i in range(rows):
            a_file.write(str(format(Ak[i][j], '.3f')) + " ")
        a_file.write("\n")

    a_file.close()






# query.dot(ak)/norm(q)*norm(ak)

