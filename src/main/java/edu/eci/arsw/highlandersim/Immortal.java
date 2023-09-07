package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback=null;
    
    private int health;
    
    private int defaultDamageValue;

    private final List<Immortal> immortalsPopulation;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());

    private boolean dead;

    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb) {
        super(name);
        this.updateCallback=ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = health;
        this.defaultDamageValue=defaultDamageValue;
    }

    public void run() {

        while (immortalsPopulation.size() > 1) {
            try {
				updateCallback.esperarSiSuspendido();	
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            Immortal im;

            int myIndex = immortalsPopulation.indexOf(this);

            int nextFighterIndex = r.nextInt(immortalsPopulation.size());

            //avoid self-fight
            if (nextFighterIndex == myIndex) {
                nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
            }

            im = immortalsPopulation.get(nextFighterIndex);

            Immortal[] peleadores = this.comparateStrings(im);
            Immortal menor = peleadores[0];
            Immortal mayor = peleadores[1];
            synchronized (menor) {
                synchronized (mayor) {
                    if (!this.dead() && !im.dead()) {
                        this.fight(im);
                    }
                }
            }
            

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    public void fight(Immortal i2) {

        if (i2.getHealth() > 0) {
            i2.changeHealth(i2.getHealth() - defaultDamageValue);
            this.health += defaultDamageValue;
            updateCallback.processReport("Fight: " + this + " vs " + i2+"\n");
            
        } else {
            i2.kill();
            updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
        }

    }
    public boolean dead(){
        return dead;
    }
    public void changeHealth(int v) {
        health = v;
    }

    public int getHealth() {
        return health;
    }
    public void kill(){
        dead = true;
        immortalsPopulation.remove(this);
    }
    public Immortal[] comparateStrings(Immortal peleador2){
        Immortal[] peleadores = new Immortal[2];
        int resultado = this.getName().compareTo(peleador2.getName());
        if (resultado <= 0) {
            peleadores[0] = this;
            peleadores[1] = peleador2;
        } else {
            peleadores[1] = this;
            peleadores[0] = peleador2;
           
        }
        return peleadores;
    }

    @Override
    public String toString() {

        return name + "[" + health + "]";
    }

}
