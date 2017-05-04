__global__ void dotProduct(double* output, const double* input, const int nx, const int ny)
{
    int i = threadIdx.x + blockIdx.x * blockDim.x; // Who am I?
    int j = threadIdx.y + blockIdx.y * blockDim.y;
    if (i >= ny || j >= ny || i < j)
    {
        return;
    }

    double acc = 0;
    for (int k = 0; k < nx; k++)
    {
        acc += input[k + j * nx] * input[k + i * nx];
    }

    output[i + j * ny] = acc;
}

void make_stuff(...---...)
{
    double* inputCPU = 0;
    double* outputCPU = 0;
    cudaMallocHost((void**) &inputCPU, ny * nx * sizeof (double));
    cudaMallocHost((void**) &outputCPU, ny * ny * sizeof (double));

    double* inputGPU = 0;
    double* outputGPU = 0;
    cudaMalloc((void**) &inputGPU, ny * nx * sizeof (double));
    cudaMalloc((void**) &outputGPU, ny * ny * sizeof (double));

    int i, j;
    double acc, mean, tmp, norm;

    for (j = 0; j < ny; j++)
    {
        acc = 0;
        for (i = 0; i < nx; i++)
        {
            acc += data[i + j * nx];
        }
        mean = acc / (double) nx;
        acc = 0;
        for (i = 0; i < nx; i++)
        {
            tmp = data[i + j * nx] - mean;
            inputCPU[i + j * nx] = tmp;
            acc += pow(tmp, 2);
        }
        norm = sqrt(acc);
        for (i = 0; i < nx; i++)
        {
            inputCPU[i + j * nx] /= norm;
        }
    }

    cudaMemcpy(inputGPU, inputCPU, ny * nx * sizeof (double), cudaMemcpyHostToDevice);
    dim3 dimBlock(8, 8);
    dim3 dimGrid((ny + dimBlock.y - 1) / dimBlock.y, (ny + dimBlock.y - 1) / dimBlock.y);

    dotProduct << <dimGrid, dimBlock>>>(outputGPU, inputGPU, nx, ny);
    cudaDeviceSynchronize();

    cudaMemcpy(outputCPU, outputGPU, ny * ny * sizeof (double), cudaMemcpyDeviceToHost);

    for (int i = 0; i < ny; i++)
    {
        for (int j = 0; j < ny; j++)
        {
            result[j + i * ny] = outputCPU[j + i * ny];
        }
    }

    cudaFreeHost(inputCPU);
    cudaFreeHost(outputCPU);
    cudaFree(inputGPU);
    cudaFree(outputGPU);
}

