import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

class ServantThread extends Thread {

    GiftChain giftChain;
    List<Integer> giftBag = new ArrayList<Integer>();
    NotePile notes;
    boolean end;

    Random random = new Random();

    public void addGiftToChain() {
        
        try {
            if(giftBag.size() > 0)
            {
                int gift = giftBag.remove(0);
                giftChain.add(gift, this.threadId());
            }
           
        } catch (IndexOutOfBoundsException e) {
            //System.out.println("Gift bag is empty.");
        }
    }

    public void removeGiftAndWriteNote() {
        int tag = giftChain.remove(this.threadId());
        
        if(tag == -1)
        {
            if(giftBag.isEmpty())
            {
                //System.out.println("Servant " + this.threadId() + " reached the end.");
                end = true;
            }
        }
        else   
        {
            notes.writeNote(this.threadId(), tag);
        }
    }

    public void checkForGift(int tag) {
        boolean found = giftChain.contains(tag);
        if (found)
            System.out.println("Servant " + this.threadId() + " found " + tag);
        else   
            System.out.println("Servant " + this.threadId() + " did not find " + tag);
    }

    synchronized public void run()
    {
        System.out.println("Servant " + this.threadId() + " began to write notes.");
        while(!end)
        {
            addGiftToChain();
            removeGiftAndWriteNote();
        }
    }
}

class NotePile {
    AtomicInteger noteCount = new AtomicInteger(0);
    
    public void writeNote(long id, int tag) {
        System.out.println("Servant " + id + " wrote the "+ noteCount.incrementAndGet() + "th note for "+ tag +".");
    }
}

class GiftNode {
    int tag;
    GiftNode next;
    AtomicBoolean lock = new AtomicBoolean(false);
    public GiftNode(int x) {
        tag = x;
    }
    public void lock() {
        while(true)
        {
            while(lock.get());
            if(!lock.getAndSet(true))
            {
                return;
            }
        }
    }
    public void unlock() {
        lock.set(false);
        
    }
}

class GiftChain {
    private GiftNode head = new GiftNode(-1);

    public boolean add(int giftTag, long id) {
        //System.out.println("Servant " + id + " is adding tag " + giftTag);
        head.lock();
        //System.out.println("Servant " + id + " achieved head lock to add " + giftTag);

        GiftNode prev = head;
        try {
            GiftNode current = head.next;
            if(current == null)
            {
                GiftNode newNode = new GiftNode(giftTag);
                newNode.next = current;
                prev.next = newNode;
                //System.out.println("Servant " + id + " added " + giftTag);
                //this.print();
                return true;
            }
            current.lock();
            //System.out.println("Servant " + id + " achieved " + current.tag + " lock to add " + giftTag);
            try {
                while(giftTag < current.tag)
                {

                    //System.out.println("Servant " + id + " unlocked " + prev.tag + " lock to continue adding " + giftTag);
                    prev.unlock();
                    prev = current;
                    current = current.next;
                    if(current == null)
                    {
                        GiftNode newNode = new GiftNode(giftTag);
                        newNode.next = current;
                        prev.next = newNode;
                        //System.out.println("Servant " + id + " added " + giftTag);
                        //this.print();
                        return true;
                    }
                    current.lock();
                    //System.out.println("Servant " + id + " achieved " + current.tag + " lock to continue adding " + giftTag);
                }
                if(current.tag == giftTag) {
                    //System.out.println("Servant " + id + " found duplicate " + giftTag);
                    return false;
                }
                GiftNode newNode = new GiftNode(giftTag);
                newNode.next = current;
                prev.next = newNode;
                //System.out.println("Servant " + id + " added " + giftTag);
                //this.print();
                return true;
            } finally {
                //System.out.println("Servant " + id + " is unlocking.");
                if(current != null)
                    current.unlock();
            }
        } finally {
            //System.out.println("Servant " + id + " unlocked " + prev.tag);
            prev.unlock();
        }
    }

    public int remove(long id) {
        GiftNode prev = null, current = null;
        //System.out.println("Servant " + id + " is removing from head.");
        head.lock();
        //System.out.println("Servant " + id + " achieved head lock.");
        try {
            prev = head;
            current = prev.next;
            if(current == null)
            {
                return -1;
            }
            int tag = current.tag;
            current.lock();
            //System.out.println("Servant " + id + " achieved " + current.tag + " lock");
            try {
                if(current.tag == tag)
                {
                    prev.next = current.next;
                    //System.out.println("Servant " + id + " removed " + tag);
                    //this.print();
                    return tag;
                }
                return -1;
            } finally {  
                //System.out.println("Servant " + id + " unlocked " + current.tag);
                current.unlock();
            }
        } finally { 
            //System.out.println("Servant " + id + " unlocked " + prev.tag);
            prev.unlock();
        }
    }

    public boolean contains(int giftTag) {
        GiftNode current = head;
        //Nothing to search
        if (current == null)
            return false;

        //Find where tag would be
        while(current.tag < giftTag && current.next != null)
        {
            current = current.next;
        }
        //If it is here return true
        if(current.tag == giftTag)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void print() {
        GiftNode current = head;
        while (current != null) {
            System.out.print(current.tag + ", ");
            current = current.next;
        }
        System.out.println();
    }
}



public class ProblemOne extends Thread {

    static void shuffleBag(int[] bag, int n)
    {
        Random rnd = new Random();
        for (int i = n - 1; i > 0; i--)
        {
            int swap = rnd.nextInt(i + 1);

            int a = bag[swap];
            bag[swap] = bag[i];
            bag[i] = a;
        }
    }

    public static void main(String[] args)
    {
        
        //Number of gifts
        int n = 500000;
        //Number of servants
        int m = 4;
        
        //Construct and shuffle bag
        List<Integer> giftBag = new ArrayList<Integer>();
        for (int i = 0; i < n; i++)
            giftBag.add(i);
        Collections.shuffle(giftBag);
        
        ServantThread servants[] = new ServantThread[m];
        GiftChain giftChain = new GiftChain();
        NotePile notes = new NotePile();

        for (int i = 0; i < m; i++) {
            servants[i] = new ServantThread();
            servants[i].giftChain = giftChain;
            servants[i].giftBag = giftBag;
            servants[i].notes = notes;
            servants[i].start();
        }

        //wait for each servant to finish
        for (int i = 0; i < m; i++) { 
            try {
                servants[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("The servants wrote " + notes.noteCount + " notes.");
        //giftChain.print();
    }
}