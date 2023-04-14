To start the first problem's code run this command:

java ProblemOne

To start the second problem's code run this command:

java ProblemTwo


For problem one, the additional thank you notes were caused by multiple servants attempting to remove and add gifts from the chain at the same time. This causes some gifts to be removed twice, or not added at all. This can be fixed by implementing locks to prevent the "nodes" on the chain from having operations performed on them by several threads simultaneously.

For problem two, my program guarantees that all temperatures are saved into a shared matrix. The data is then copied for calculations at every hour, so that calculations can be performed even when new data is overwriting the old data. This allows rovers to work continuously with no delays.