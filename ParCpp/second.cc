
Class1 make_stuff(...---...)
{
    int prec_cols = nx + 2;
    int prec_rows = ny + 2;
    double4_t* vec_data = double4_alloc(ny * nx);
    double4_t* precomp_vals = double4_alloc(prec_rows * prec_cols);

#pragma omp parallel 
    {
#pragma omp for nowait
        for (int i = 0; i < ny; i++)
        {
            for (int j = 0; j < nx; j++)
            {
                for (int k = 0; k < 3; k++)
                {
                    vec_data[j + i * nx][k] = data[k + 3 * j + 3 * i * nx];
                }
            }
        }

#pragma omp for nowait
        for (int i = 0; i < prec_rows; i++)
        {
            precomp_vals[i * prec_cols] = double4_0;
            precomp_vals[prec_cols - 1 + i * prec_cols] = double4_0;
        }
#pragma omp for nowait
        for (int i = 1; i < prec_cols; i++)
        {
            precomp_vals[i] = double4_0;
            precomp_vals[i + (prec_rows - 1) * prec_cols] = double4_0;
        }
    }

    for (int i = 0; i < ny; i++)
    {
        for (int j = 0; j < nx; j++)
        {
            precomp_vals[j + 1 + (i + 1) * prec_cols] = precomp_vals[j + (i + 1) * prec_cols] + precomp_vals[j + 1 + i * prec_cols] -
                    precomp_vals[j + i * prec_cols] + vec_data[j + i * nx];
        }
    }

    double4_t vp = precomp_vals[nx + ny * prec_cols];

    int tmax = omp_get_max_threads();
    double* vec_max = new double[tmax * 4];
    for (int i = 0; i < tmax; i++)
    {
        for (int j = 0; j < 4; j++)
        {
            vec_max[j + 4 * i] = 0;
        }
    }
    int* vec_x0 = new int[tmax * 4];
    int* vec_y0 = new int[tmax * 4];
    int* vec_x1 = new int[tmax * 4];
    int* vec_y1 = new int[tmax * 4];

#pragma omp parallel
    {
        double4_t vec_cache_8[] = {double4_0, double4_0, double4_0, double4_0, double4_0, double4_0, double4_0, double4_0};
        double4_t vec_vx[] = {double4_0, double4_0, double4_0, double4_0};
        double4_t vec_vy[] = {double4_0, double4_0, double4_0, double4_0};
        double4_t tmp[] = {double4_0, double4_0, double4_0, double4_0};

        int t = omp_get_thread_num();
        int x0, x1, y0, y1;
#pragma omp for schedule (dynamic)
        for (int k = 1; k <= ny; k++)
        {
            for (int l = 1; l <= nx; l += 2)
            {
                double4_t xFac;
                double4_t yFac;
                xFac[0] = xFac[3] = 1 / (double) (k * l);
                xFac[1] = 1 / (double) (k * (l + 1));
                xFac[2] = 1 / (double) (k * (l - 1));

                if ((nx * ny) - (k * l) == 0)
                {
                    yFac[0] = yFac[3] = 0;
                    yFac[2] = 1 / (double) ((nx * ny) - (k * (l - 1)));
                }
                else
                {
                    yFac[0] = yFac[3] = 1 / (double) ((nx * ny) - (k * l));
                    yFac[1] = 1 / (double) ((nx * ny) - (k * (l + 1)));
                    yFac[2] = 1 / (double) ((nx * ny) - (k * (l - 1)));
                }

                for (int i = 0; i <= ny - k; i++)
                {
                    for (int j = 0; j <= nx - l; j += 2)
                    {
                        x0 = j;
                        y0 = i;
                        x1 = j + l;
                        y1 = i + k;

                        vec_cache_8[0] = precomp_vals[x0 + y0 * prec_cols];
                        vec_cache_8[1] = precomp_vals[x1 + y0 * prec_cols];
                        vec_cache_8[2] = precomp_vals[x0 + y1 * prec_cols];
                        vec_cache_8[3] = precomp_vals[x1 + y1 * prec_cols];

                        vec_cache_8[4] = precomp_vals[x0 + 1 + y0 * prec_cols];
                        vec_cache_8[5] = precomp_vals[x1 + 1 + y0 * prec_cols];
                        vec_cache_8[6] = precomp_vals[x0 + 1 + y1 * prec_cols];
                        vec_cache_8[7] = precomp_vals[x1 + 1 + y1 * prec_cols];

                        vec_vx[0] = vec_cache_8[3] + vec_cache_8[0] - vec_cache_8[2] - vec_cache_8[1];
                        vec_vy[0] = vp - vec_vx[0];

                        vec_vx[1] = vec_cache_8[7] + vec_cache_8[0] - vec_cache_8[2] - vec_cache_8[5];
                        vec_vy[1] = vp - vec_vx[1];

                        vec_vx[2] = vec_cache_8[3] + vec_cache_8[4] - vec_cache_8[6] - vec_cache_8[1];
                        vec_vy[2] = vp - vec_vx[2];

                        vec_vx[3] = vec_cache_8[7] + vec_cache_8[4] - vec_cache_8[6] - vec_cache_8[5];
                        vec_vy[3] = vp - vec_vx[3];

                        tmp[0] = vec_vx[0] * vec_vx[0] * xFac[0] + vec_vy[0] * vec_vy[0] * yFac[0];
                        tmp[1] = vec_vx[1] * vec_vx[1] * xFac[1] + vec_vy[1] * vec_vy[1] * yFac[1];
                        tmp[2] = vec_vx[2] * vec_vx[2] * xFac[2] + vec_vy[2] * vec_vy[2] * yFac[2];
                        tmp[3] = vec_vx[3] * vec_vx[3] * xFac[3] + vec_vy[3] * vec_vy[3] * yFac[3];

                        if (tmp[0][0] + tmp[0][1] + tmp[0][2] >= vec_max[t * 4])
                        {
                            vec_max[t * 4] = tmp[0][0] + tmp[0][1] + tmp[0][2];
                            vec_x0[t * 4] = x0;
                            vec_y0[t * 4] = y0;
                            vec_x1[t * 4] = x1;
                            vec_y1[t * 4] = y1;
                        }
                        if (j != nx - l)
                        {
                            if (tmp[1][0] + tmp[1][1] + tmp[1][2] >= vec_max[1 + t * 4])
                            {
                                vec_max[1 + t * 4] = tmp[1][0] + tmp[1][1] + tmp[1][2];
                                vec_x0[1 + t * 4] = x0;
                                vec_y0[1 + t * 4] = y0;
                                vec_x1[1 + t * 4] = x1 + 1;
                                vec_y1[1 + t * 4] = y1;
                            }
                            if (l > 1 && tmp[2][0] + tmp[2][1] + tmp[2][2] >= vec_max[2 + t * 4])
                            {
                                vec_max[2 + t * 4] = tmp[2][0] + tmp[2][1] + tmp[2][2];
                                vec_x0[2 + t * 4] = x0 + 1;
                                vec_y0[2 + t * 4] = y0;
                                vec_x1[2 + t * 4] = x1;
                                vec_y1[2 + t * 4] = y1;
                            }
                            if (tmp[3][0] + tmp[3][1] + tmp[3][2] >= vec_max[3 + t * 4])
                            {
                                vec_max[3 + t * 4] = tmp[3][0] + tmp[3][1] + tmp[3][2];
                                vec_x0[3 + t * 4] = x0 + 1;
                                vec_y0[3 + t * 4] = y0;
                                vec_x1[3 + t * 4] = x1 + 1;
                                vec_y1[3 + t * 4] = y1;
                            }
                        }
                    }
                }
            }
        }
    }
    double max = 0;
    int index = 0;
    for (int i = 0; i < tmax * 4; i++)
    {
        if (vec_max[i] > max)
        {
            max = vec_max[i];
            index = i;
        }
    }
    int x0 = vec_x0[index];
    int x1 = vec_x1[index];
    int y0 = vec_y0[index];
    int y1 = vec_y1[index];

    delete [] vec_max;
    delete [] vec_x0;
    delete [] vec_x1;
    delete [] vec_y0;
    delete [] vec_y1;

    double4_t vx = precomp_vals[x1 + y1 * prec_cols] + precomp_vals[x0 + y0 * prec_cols] - precomp_vals[x0 + y1 * prec_cols] - precomp_vals[x1 + y0 * prec_cols];
    double4_t vy = vp - vx;

    double xFac = 1 / (double) ((x1 - x0) * (y1 - y0));
    double yFac = 1 / (double) ((nx * ny) - (x1 - x0) * (y1 - y0));

    vx *= xFac;
    vy *= yFac;

    free(vec_data);
    free(precomp_vals);

    return Class1{y0, x0, y1, x1,
        {(float) vy[0], (float) vy[1], (float) vy[2]},
        {(float) vx[0], (float) vx[1], (float) vx[2]}};
}
