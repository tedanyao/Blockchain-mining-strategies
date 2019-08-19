package edu.nyu.crypto.csci3033.miners;

import edu.nyu.crypto.csci3033.blockchain.Block;
import edu.nyu.crypto.csci3033.blockchain.NetworkStatistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class FeeSnipingMiner extends BaseMiner implements Miner {
    private Block currentHead;
    private double alpha;

    public FeeSnipingMiner(String id, int hashRate, int connectivity) {
        super(id, hashRate, connectivity);
    }

    @Override
    public Block currentlyMiningAt() {
        return currentHead;
    }

    @Override
    public Block currentHead() {
        // which is for broadcasting to others
        return currentHead;
    }

    @Override
    public void blockMined(Block block, boolean isMinerMe) {
        if(isMinerMe) {
            if (block.getHeight() > currentHead.getHeight()) {
                this.currentHead = block;
            }
        }
        else{
            if (currentHead == null) {
                currentHead = block;
            }
            else if (block != null) {
                if (block.getHeight() > currentHead.getHeight()) {
                    // the larger the alpha, the lower target value we can perform snipping
                    if (block.getBlockValue() > 3.0/alpha) {
                        this.currentHead = block.getPreviousBlock();
                    }
                    else if (block.getHeight() - currentHead.getHeight() > 0) {
                        this.currentHead = block;
                    }
                }
            }
        }



    }


    @Override
    public void initialize(Block genesis, NetworkStatistics networkStatistics) {

        this.currentHead = genesis;
        networkUpdate(networkStatistics);
    }

    @Override
    public void networkUpdate(NetworkStatistics statistics) {
        double totalHashRate = statistics.getTotalHashRate();
        double myHashRate = getHashRate();
        alpha = myHashRate/totalHashRate;
    }
}
