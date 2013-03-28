from matplotlib.pyplot import *
import numpy as np

big = 1.1
figure(figsize=(big*5,big*2))
ax = axes()
ax.set_position([0.15, 0.2, 0.8, 0.7])

T = 2
max_seq = 2**4
max_time = 40

ax.plot(np.arange(max_seq), color = 'black')
ax.plot(np.arange(T, max_seq + T), np.arange(max_seq), color = 'black')
ax.plot(np.arange(max_seq, 2*max_seq), np.arange(max_seq), color = 'black')
ax.plot(np.arange(T+max_seq, 2*max_seq + T), np.arange(max_seq), color = 'black')
ax.plot(np.arange(5, 26), np.arange(3, 9.1, 6.0/20), linewidth = 2.0,
	color = 'green')
ax.plot(np.arange(5, 9), np.arange(3, 9, 5.0/3), linewidth = 2.0,
	color = 'blue')
ax.fill_between(np.arange(max_seq+T),
	np.append((T)*[0], np.arange(max_seq)),
	np.append(np.arange(max_seq), (T)*[max_seq-1]), color = 'gray')
ax.fill_between(np.arange(max_seq, 2*max_seq+T),
	np.append((T)*[0], np.arange(max_seq)),
	np.append(np.arange(max_seq), (T)*[max_seq-1]), color = 'gray')


xlabel('Time (s)')
ylabel('Sequence numbers', multialignment='center')
grid(True)

savefig('forbiddenregion.png')
