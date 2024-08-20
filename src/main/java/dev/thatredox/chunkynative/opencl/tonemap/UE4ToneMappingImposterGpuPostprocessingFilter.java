package dev.thatredox.chunkynative.opencl.tonemap;

import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_kernel;
import se.llbit.chunky.renderer.postprocessing.PostProcessingFilter;
import se.llbit.chunky.renderer.postprocessing.UE4ToneMappingFilter;
import se.llbit.chunky.resources.BitmapImage;
import se.llbit.util.Configurable;
import se.llbit.util.TaskTracker;

import static org.jocl.CL.clSetKernelArg;

public class UE4ToneMappingImposterGpuPostprocessingFilter extends UE4ToneMappingFilter implements PostProcessingFilter, Configurable {
    private class Inner extends SimpleGpuPostProcessingFilter {
        public Inner(String name, String description, String id, String entryPoint) {
            super(name, description, id, entryPoint);
        }

        @Override
        protected void addArguments(cl_kernel kernel) {
            int arg = 5;
            setFloat(kernel, arg++, UE4ToneMappingImposterGpuPostprocessingFilter.this.getSaturation());
            setFloat(kernel, arg++, UE4ToneMappingImposterGpuPostprocessingFilter.this.getSlope());
            setFloat(kernel, arg++, UE4ToneMappingImposterGpuPostprocessingFilter.this.getToe());
            setFloat(kernel, arg++, UE4ToneMappingImposterGpuPostprocessingFilter.this.getShoulder());
            setFloat(kernel, arg++, UE4ToneMappingImposterGpuPostprocessingFilter.this.getBlackClip());
            setFloat(kernel, arg++, UE4ToneMappingImposterGpuPostprocessingFilter.this.getWhiteClip());
            setFloat(kernel, arg++, (1f - UE4ToneMappingImposterGpuPostprocessingFilter.this.getToe() - 0.18f) / UE4ToneMappingImposterGpuPostprocessingFilter.this.getSlope() - 0.733f);
            setFloat(kernel, arg, (UE4ToneMappingImposterGpuPostprocessingFilter.this.getShoulder() - 0.18f) / UE4ToneMappingImposterGpuPostprocessingFilter.this.getSlope() - 0.733f);
        }

        private void setFloat(cl_kernel kernel, int arg, float value) {
            clSetKernelArg(kernel, arg, Sizeof.cl_float, Pointer.to(new float[] { value }));
        }
    }

    private final Inner inner;

    public UE4ToneMappingImposterGpuPostprocessingFilter(UE4ToneMappingFilter imposter) {
        this.inner = new Inner(imposter.getName(), imposter.getDescription(), imposter.getId(), "ue4_filter");
        this.setSaturation(imposter.getSaturation());
        this.setSlope(imposter.getSlope());
        this.setToe(imposter.getToe());
        this.setShoulder(imposter.getShoulder());
        this.setBlackClip(imposter.getBlackClip());
        this.setWhiteClip(imposter.getWhiteClip());
    }

    @Override
    public void processFrame(int width, int height, double[] input, BitmapImage output, double exposure, TaskTracker.Task task) {
        inner.processFrame(width, height, input, output, exposure, task);
    }

    @Override
    public String getName() {
        return inner.getName();
    }

    @Override
    public String getDescription() {
        return inner.getDescription();
    }

    @Override
    public String getId() {
        return inner.getId();
    }
}
