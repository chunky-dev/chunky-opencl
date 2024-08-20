package dev.thatredox.chunkynative.opencl.tonemap;

import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_kernel;
import se.llbit.chunky.renderer.postprocessing.PostProcessingFilter;

import static org.jocl.CL.clSetKernelArg;

public class ChunkyImposterGpuPostProcessingFilter extends SimpleGpuPostProcessingFilter {
    public enum Filter {
        GAMMA(0),
        TONEMAP1(1),
        ACES(2),
        HABLE(3);

        public final int id;
        Filter(int id) {
            this.id = id;
        }
    }

    private final Filter filter;

    public ChunkyImposterGpuPostProcessingFilter(PostProcessingFilter imposter, Filter filter) {
        super(imposter.getName(), imposter.getDescription(), imposter.getId(), "filter");
        this.filter = filter;
    }

    @Override
    protected void addArguments(cl_kernel kernel) {
        clSetKernelArg(kernel, 5, Sizeof.cl_int, Pointer.to(new int[] { filter.id }));
    }
}
