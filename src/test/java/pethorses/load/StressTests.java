package pethorses.load;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import pethorses.PetHorses;
import pethorses.services.HorseService;
import pethorses.services.HorseBackpackService;
import pethorses.storage.HorseData;
import pethorses.storage.HorseDataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class StressTests {
    private PetHorses plugin;
    private HorseService horseService;
    private HorseBackpackService backpackService;
    private HorseDataManager dataManager;

    private int getIntProp(String name, int defaultValue) {
        String v = System.getProperty(name);
        if (v == null) return defaultValue;
        try { return Integer.parseInt(v); } catch (NumberFormatException e) { return defaultValue; }
    }

    @BeforeEach
    public void setUp() {
        try { MockBukkit.unmock(); } catch (IllegalStateException ignored) {}
        MockBukkit.mock();
        plugin = MockBukkit.load(PetHorses.class);
        horseService = plugin.getHorseService();
        backpackService = new HorseBackpackService(plugin);
        dataManager = plugin.getHorseDataManager();
    }

    @AfterEach
    public void tearDown() {
        try { MockBukkit.unmock(); } catch (IllegalStateException ignored) {}
    }

    @Test
    public void concurrentHorseDataAccessStress() throws InterruptedException {
        int threads = getIntProp("stress.threads", 8);
        int iterations = getIntProp("stress.iterations", 200);

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        List<Future<?>> futures = new ArrayList<>();
        ConcurrentLinkedQueue<Throwable> errors = new ConcurrentLinkedQueue<>();

        try {
            for (int i = 0; i < threads; i++) {
                futures.add(executor.submit(() -> {
                    try {
                        for (int j = 0; j < iterations; j++) {
                            UUID id = UUID.randomUUID();
                            HorseData data = dataManager.getHorseData(id);
                            data.setLevel((int) (Math.random() * 100));
                            data.setExperience((int) (Math.random() * 1000));
                            dataManager.saveHorseData(data);
                            horseService.addJump(id);
                            horseService.addTraveledBlocks(id, Math.random() * 10);
                        }
                    } catch (Throwable t) {
                        errors.add(t);
                    }
                }));
            }

            for (Future<?> f : futures) {
                try { f.get(5, TimeUnit.MINUTES); } catch (TimeoutException e) { errors.add(e); } catch (ExecutionException e) { errors.add(e.getCause()); }
            }

            if (!errors.isEmpty()) {
                Throwable t = errors.peek();
                fail("One or more worker tasks failed: " + t);
            }
        } finally {
            executor.shutdown();
            if (!executor.awaitTermination(5, TimeUnit.MINUTES)) {
                List<Runnable> dropped = executor.shutdownNow();
                if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                    fail("Executor did not terminate; dropped tasks: " + dropped.size());
                }
            }
        }
    }

    @Test
    public void highFrequencySaveAndLoad() throws InterruptedException {
        int threads = getIntProp("stress.threads", 8);
        int iterations = getIntProp("stress.iterations", 500);

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        List<Callable<Void>> tasks = new ArrayList<>();
        ConcurrentLinkedQueue<Throwable> errors = new ConcurrentLinkedQueue<>();

        try {
            for (int i = 0; i < threads; i++) {
                tasks.add(() -> {
                    try {
                        for (int j = 0; j < iterations; j++) {
                            UUID id = UUID.randomUUID();
                            HorseData data = dataManager.getHorseData(id);
                            data.setBackpackItems(new org.bukkit.inventory.ItemStack[9]);
                            dataManager.saveHorseData(data);
                        }
                    } catch (Throwable t) {
                        errors.add(t);
                    }
                    return null;
                });
            }

            List<Future<Void>> results = executor.invokeAll(tasks, 10, TimeUnit.MINUTES);

            for (Future<Void> r : results) {
                if (r.isCancelled()) errors.add(new CancellationException("Task cancelled"));
                try { r.get(); } catch (ExecutionException e) { errors.add(e.getCause()); }
            }

            if (!errors.isEmpty()) {
                Throwable t = errors.peek();
                fail("One or more worker tasks failed: " + t);
            }
        } finally {
            executor.shutdown();
            if (!executor.awaitTermination(5, TimeUnit.MINUTES)) {
                List<Runnable> dropped = executor.shutdownNow();
                if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                    fail("Executor did not terminate; dropped tasks: " + dropped.size());
                }
            }
        }
    }

    @Test
    public void backpackServiceConcurrentArmorSaves() throws InterruptedException {
        int threads = getIntProp("stress.threads", 12);
        int iterations = getIntProp("stress.iterations", 200);

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        List<Future<?>> futures = new ArrayList<>();
        ConcurrentLinkedQueue<Throwable> errors = new ConcurrentLinkedQueue<>();

        try {
            for (int i = 0; i < threads; i++) {
                futures.add(executor.submit(() -> {
                    try {
                        for (int j = 0; j < iterations; j++) {
                            UUID id = UUID.randomUUID();
                            backpackService.saveHorseArmor(id, null);
                        }
                    } catch (Throwable t) {
                        errors.add(t);
                    }
                }));
            }

            for (Future<?> f : futures) {
                try { f.get(3, TimeUnit.MINUTES); } catch (TimeoutException e) { errors.add(e); } catch (ExecutionException e) { errors.add(e.getCause()); }
            }

            if (!errors.isEmpty()) {
                Throwable t = errors.peek();
                fail("One or more worker tasks failed: " + t);
            }
        } finally {
            executor.shutdown();
            if (!executor.awaitTermination(5, TimeUnit.MINUTES)) {
                List<Runnable> dropped = executor.shutdownNow();
                if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                    fail("Executor did not terminate; dropped tasks: " + dropped.size());
                }
            }
        }
    }
}
