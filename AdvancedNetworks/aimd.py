from matplotlib.pyplot import *
import numpy as np

big = 2
figure(figsize=(big*3,big*3))
xlabel('Bandwidth of connection 1 (Mbps)')
ylabel('Bandwidth of connection 2 (Mbps)')
axis([0, 200, 0, 200])
grid()

x = [16]
y = [84]

additive = 15
multiplicative = 4.0/5
bandwidth = 200

for i in xrange(20):
	if x[i]+y[i] > bandwidth:
		x.append(multiplicative * x[i])
		y.append(multiplicative * y[i])
	else:
		x.append(additive + x[i])
		y.append(additive + y[i])

X = np.asarray(x)
Y = np.asarray(y)


quiver(X[:-1], Y[:-1], X[1:] - X[:-1], Y[1:] - Y[:-1],
	scale_units='xy', angles='xy', scale=1, width=0.006)
scatter(X, Y, s = 20, color = 'black')


fairness = np.arange(200)
efficiency = np.arange(200, 0, -1)

plot(fairness, '--')
plot(efficiency, '--')

text(150, 150, 'Fairness', rotation = 45)
text(150, 50, 'Efficiency', rotation = -45)

scatter([100], [100], s = 60, color = 'black')
text(105, 100, 'Optimal point')

savefig('aimd.png')
